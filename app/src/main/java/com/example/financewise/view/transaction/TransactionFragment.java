package com.example.financewise.view.transaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financewise.R;
import com.example.financewise.adapter.TransactionAdapter;
import com.example.financewise.databinding.FragmentTransactionBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TransactionFragment extends BaseFragment<FragmentTransactionBinding, HomeViewModel> {
    private static final String TAG = "TransactionFragment";
    private static final String ARG_USER_ID = "userId";
    private TransactionAdapter adapter;
    private boolean isIncomeSelected = false;
    private boolean isExpenseSelected = false;
    private Long selectedDateTimestamp = null;
    private NavigationManager navigationManager;
    private List<HomeViewModel.TransactionItem> allTransactions = new ArrayList<>();

    public static TransactionFragment newInstance(String userId) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentTransactionBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        Log.d(TAG, "getViewBinding called");
        FragmentTransactionBinding binding = FragmentTransactionBinding.inflate(inflater, container, false);
        Log.d(TAG, "getViewBinding returned: " + (binding != null ? "not null" : "null"));
        return binding;
    }

    @Override
    protected Class<HomeViewModel> getViewModelClass() {
        return HomeViewModel.class;
    }

    @Override
    protected void setupUI() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in setupUI, skipping UI initialization");
            return;
        }

        String userId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : null;
        if (userId == null) {
            Log.e(TAG, "userId is null, fragment may not function correctly");
            return;
        }
        viewModel.setUserId(userId);

        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        binding.rvTransactionList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactionList.setAdapter(adapter);
        adapter.submitList(new ArrayList<>()); // Khởi tạo với danh sách rỗng

        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);

        setupClickListeners();
    }

    private void setupClickListeners() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in setupClickListeners");
            return;
        }

        binding.tvBackIcon.setOnClickListener(v -> {
            Fragment homeFragment = HomeFragment.newInstance(getArguments() != null ? getArguments().getString(ARG_USER_ID) : null);
            navigationManager.navigateWithSlideAnimation(homeFragment, false);
        });

        binding.addBtn.setOnClickListener(v -> {
            Fragment addTransactionFragment = AddTransactionFragment.newInstance(getArguments() != null ? getArguments().getString(ARG_USER_ID) : null);
            navigationManager.navigateWithSlideAnimation(addTransactionFragment, true);
        });

        binding.calenderBtn.setOnClickListener(v -> showDatePickerDialog());

        binding.llIncome.setOnClickListener(v -> toggleFilter(true));

        binding.llExpense.setOnClickListener(v -> toggleFilter(false));
    }

    @Override
    protected void observeViewModel() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in observeViewModel");
            return;
        }

        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                binding.tvTotalBalanceValue.setText(balance);
            }
        });

        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            if (income != null) {
                binding.tvTotalIncome.setText(formatCurrency(income));
            }
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                binding.tvTotalExpense.setText(formatCurrency(Long.parseLong(expense.replace(",", ""))));
            }
        });

        viewModel.getAllTransaction().observe(getViewLifecycleOwner(), transactionItems -> { // Sửa thành getAllTransactions
            Log.d(TAG, "All Transactions observed, size: " + (transactionItems != null ? transactionItems.size() : 0));
            if (transactionItems != null) {
                allTransactions.clear();
                allTransactions.addAll(transactionItems);
                // Sắp xếp theo ngày giảm dần (gần nhất đến xa nhất)
                Collections.sort(allTransactions, new Comparator<HomeViewModel.TransactionItem>() {
                    @Override
                    public int compare(HomeViewModel.TransactionItem item1, HomeViewModel.TransactionItem item2) {
                        return item2.getDate().compareTo(item1.getDate());
                    }
                });
                // Áp dụng bộ lọc hiện tại (nếu có)
                applyCurrentFilter();
                updateEmptyView(allTransactions);
            } else {
                Log.w(TAG, "TransactionItems is null");
                updateEmptyView(new ArrayList<>());
            }
        });
    }

    private void toggleFilter(boolean isIncome) {
        if (binding == null) {
            Log.e(TAG, "Binding is null in toggleFilter");
            return;
        }
        if (isIncome) {
            if (isIncomeSelected) {
                resetFilter();
            } else {
                isIncomeSelected = true;
                isExpenseSelected = false;
                selectedDateTimestamp = null; // Reset ngày khi chọn income/expense
            }
        } else {
            if (isExpenseSelected) {
                resetFilter();
            } else {
                isExpenseSelected = true;
                isIncomeSelected = false;
                selectedDateTimestamp = null; // Reset ngày khi chọn income/expense
            }
        }
        applyCurrentFilter();
        updateUI();
    }

    private void applyCurrentFilter() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in applyCurrentFilter");
            return;
        }
        List<HomeViewModel.TransactionItem> filteredList = new ArrayList<>(allTransactions);
        if (isIncomeSelected) {
            filteredList.removeIf(item -> item.getAmount() < 0);
        } else if (isExpenseSelected) {
            filteredList.removeIf(item -> item.getAmount() >= 0);
        }
        if (selectedDateTimestamp != null) {
            filteredList.removeIf(item -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(item.getDate().toDate());
                Calendar selectCal = Calendar.getInstance();
                selectCal.setTimeInMillis(selectedDateTimestamp);
                return cal.get(Calendar.DAY_OF_YEAR) != selectCal.get(Calendar.DAY_OF_YEAR) ||
                        cal.get(Calendar.YEAR) != selectCal.get(Calendar.YEAR);
            });
        }
        adapter.submitList(new ArrayList<>(filteredList));
        updateEmptyView(filteredList);
    }

    private void resetFilter() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in resetFilter");
            return;
        }
        isIncomeSelected = false;
        isExpenseSelected = false;
        selectedDateTimestamp = null;
        applyCurrentFilter();
        updateUI();
    }

    private void showDatePickerDialog() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in showDatePickerDialog");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth);
                    selectedDateTimestamp = selectedCal.getTimeInMillis();
                    isIncomeSelected = false;
                    isExpenseSelected = false;
                    applyCurrentFilter();
                    updateUI();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private List<HomeViewModel.TransactionItem> filterTransactions(List<HomeViewModel.TransactionItem> transactionItemList) {
        if (transactionItemList == null) return new ArrayList<>();
        List<HomeViewModel.TransactionItem> filteredList = new ArrayList<>(transactionItemList);
        if (selectedDateTimestamp != null) {
            filteredList.removeIf(item -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(item.getDate().toDate());
                Calendar selectCal = Calendar.getInstance();
                selectCal.setTimeInMillis(selectedDateTimestamp);
                return cal.get(Calendar.DAY_OF_YEAR) != selectCal.get(Calendar.DAY_OF_YEAR) ||
                        cal.get(Calendar.YEAR) != selectCal.get(Calendar.YEAR);
            });
        }
        if (isIncomeSelected) {
            filteredList.removeIf(item -> item.getAmount() < 0);
        }
        if (isExpenseSelected) {
            filteredList.removeIf(item -> item.getAmount() >= 0);
        }
        filteredList.sort((item1, item2) -> item2.getDate().compareTo(item1.getDate()));
        return filteredList;
    }

    private void updateUI() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in updateUI");
            return;
        }
        if (isIncomeSelected || isExpenseSelected || selectedDateTimestamp != null) {
            int selectedBackground = R.drawable.rounded_ocean_blue_tvbg;
            binding.llIncome.setBackgroundResource(isIncomeSelected ? selectedBackground : R.drawable.rounded_green_white_tvbg);
            binding.llExpense.setBackgroundResource(isExpenseSelected ? selectedBackground : R.drawable.rounded_green_white_tvbg);

            if (isIncomeSelected) {
                binding.imgIncome.setImageResource(R.drawable.ic_income_white);
                binding.tvIncome.setTextColor(getResources().getColor(android.R.color.white));
                binding.tvTotalIncome.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                binding.imgIncome.setImageResource(R.drawable.ic_income_main_green);
                binding.tvIncome.setTextColor(getResources().getColor(android.R.color.black));
                binding.tvTotalIncome.setTextColor(getResources().getColor(android.R.color.black));
            }

            if (isExpenseSelected) {
                binding.imgExpense.setImageResource(R.drawable.ic_expense_white);
                binding.tvExpense.setTextColor(getResources().getColor(android.R.color.white));
                binding.tvTotalExpense.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                binding.imgExpense.setImageResource(R.drawable.ic_expense_blue);
                binding.tvExpense.setTextColor(getResources().getColor(android.R.color.black));
                binding.tvTotalExpense.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else {
            resetUI();
        }
    }

    private void resetUI() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in resetUI");
            return;
        }
        binding.llIncome.setBackgroundResource(R.drawable.rounded_green_white_tvbg);
        binding.llExpense.setBackgroundResource(R.drawable.rounded_green_white_tvbg);
        binding.imgIncome.setImageResource(R.drawable.ic_income_main_green);
        binding.tvIncome.setTextColor(getResources().getColor(android.R.color.black));
        binding.tvTotalIncome.setTextColor(getResources().getColor(android.R.color.black));
        binding.imgExpense.setImageResource(R.drawable.ic_expense_blue);
        binding.tvExpense.setTextColor(getResources().getColor(android.R.color.black));
        binding.tvTotalExpense.setTextColor(getResources().getColor(android.R.color.black));
    }

    @SuppressLint("SetTextI18n")
    private void updateEmptyView(List<?> list) {
        if (binding == null) {
            Log.e(TAG, "Binding is null in updateEmptyView");
            return;
        }
        if (list.isEmpty()) {
            binding.rvTransactionList.setVisibility(View.GONE);
            TextView emptyView = binding.getRoot().findViewById(R.id.tv_empty_view); // Đảm bảo ID đúng
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("No transactions available");
            } else {
                Log.e(TAG, "tv_empty_view not found in layout");
            }
        } else {
            binding.rvTransactionList.setVisibility(View.VISIBLE);
            TextView emptyView = binding.getRoot().findViewById(R.id.tv_empty_view);
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    private String formatCurrency(long amount) {
        return String.format("%,d", amount);
    }
}
package com.example.financewise.view.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;
import com.example.financewise.adapter.TransactionAdapter;
import com.example.financewise.databinding.FragmentHomeBinding;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.viewmodel.HomeViewModel;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private static final String TAG = "HomeFragment";
    private static final String ARG_USER_ID = "userId";
    private TransactionAdapter adapter;

    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentHomeBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<HomeViewModel> getViewModelClass() {
        return HomeViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        observeViewModel();
    }

    @Override
    protected void setupUI() {
        String userId = getArguments() != null ? getArguments().getString("userId") : null;
        Log.d(TAG, "setupUI: userId = " + userId);
        if (userId == null) {
            Log.e(TAG, "userId is null, navigating back to login");
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_login);
            return;
        }

        viewModel.setUserId(userId); // This triggers loadData immediately

        // Setup RecyclerView
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        binding.rvTransactionList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactionList.setAdapter(adapter);

        // Setup notification icon click
        binding.ivNotificationIcon.setOnClickListener(v -> {});

        // Setup period buttons
        setupPeriodButtons();
    }

    @Override
    protected void observeViewModel() {
        Log.d(TAG, "observeViewModel started");
        if (viewModel == null) {
            Log.e(TAG, "viewModel is null");
            return;
        }

        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            Log.d(TAG, "Transactions updated, size: " + (transactions != null ? transactions.size() : 0));
            if (adapter != null) {
                try {
                    adapter.submitList(transactions != null ? new ArrayList<>(transactions) : new ArrayList<>());
                } catch (Exception e) {
                    Log.e(TAG, "Error updating adapter: " + e.getMessage());
                }
            }
        });

        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            Log.d(TAG, "Total Balance updated: " + balance);
            try {
                binding.tvTotalBalanceValue.setText(balance != null ? balance : "0");
            } catch (Exception e) {
                Log.e(TAG, "Error setting total balance: " + e.getMessage());
            }
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            Log.d(TAG, "Total Expense updated: " + expense);
            try {
                binding.tvTotalExpenseValue.setText(expense != null ? expense : "0");
            } catch (Exception e) {
                Log.e(TAG, "Error setting total expense: " + e.getMessage());
            }
        });

        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> { // Observe totalIncome
            try {
                binding.tvTotalGoal.setText(formatCurrency(income != null ? income : 0L)); // Update tv_total_goal
                Log.d(TAG, "tvTotalGoal updated to: " + formatCurrency(income != null ? income : 0L));
            } catch (Exception e) {
                Log.e(TAG, "Error setting total goal: " + e.getMessage());
            }
        });

        viewModel.getTotalIncomeLastWeek().observe(getViewLifecycleOwner(), income -> {
            try {
                binding.tvRevenueValue.setText(formatCurrency(income));
            } catch (Exception e) {
                Log.e(TAG, "Error setting revenue value: " + e.getMessage());
            }
        });

        // Handle food expense for last week
        viewModel.getTotalFoodLastWeek().observe(getViewLifecycleOwner(), foodExpense -> {
            try {
                binding.tvFoodValue.setText(formatCurrency(foodExpense));
            } catch (Exception e) {
                Log.e(TAG, "Error setting food value: " + e.getMessage());
            }
        });

        viewModel.getExpensePercentage().observe(getViewLifecycleOwner(), percentage -> {
            try {
                binding.tvProgressPercentage.setText(String.format("%.0f%%", percentage));
                binding.tvExpenseStatus.setText(getExpenseStatusText(percentage));
                updateProgressBarGuideline(percentage);
            } catch (Exception e) {
                Log.e(TAG, "Error setting progress data: " + e.getMessage());
            }
        });
    }

    private void setupPeriodButtons() {
        View.OnClickListener periodClickListener = v -> {
            String period = v.getId() == R.id.btn_daily ? "daily" :
                    v.getId() == R.id.btn_weekly ? "weekly" : "monthly";
            Log.d(TAG, "Period selected: " + period);
            viewModel.setSelectedPeriod(period);
            updateButtonStyles(period);
        };

        binding.btnDaily.setOnClickListener(periodClickListener);
        binding.btnWeekly.setOnClickListener(periodClickListener);
        binding.btnMonthly.setOnClickListener(periodClickListener);

        // Default to daily
        updateButtonStyles("daily");
    }

    private void updateButtonStyles(String selectedPeriod) {
        binding.btnDaily.setBackgroundResource(
                "daily".equals(selectedPeriod) ? R.drawable.rounded_main_green_tvbg : R.drawable.rounded_light_green_background);
        binding.btnWeekly.setBackgroundResource(
                "weekly".equals(selectedPeriod) ? R.drawable.rounded_main_green_tvbg : R.drawable.rounded_light_green_background);
        binding.btnMonthly.setBackgroundResource(
                "monthly".equals(selectedPeriod) ? R.drawable.rounded_main_green_tvbg : R.drawable.rounded_light_green_background);
    }

    private void updateProgressBarGuideline(float percentage) {
        if(percentage/100 >= 0.18){
            binding.guidelineEnd.setGuidelinePercent(percentage/100f);
        }else{
            binding.guidelineEnd.setGuidelinePercent(18/100f);
        }
    }

    private String getExpenseStatusText(float percentage) {
        if (percentage < 50) {
            return "Less than 50% of your expenses, looking good!";
        } else if (percentage < 80) {
            return "Over 50% of your expenses, be cautious!";
        } else {
            return "Over 80% of your expenses, take action!";
        }
    }

    private String formatCurrency(long amount) {
        return String.format("%,d", amount);
    }
}
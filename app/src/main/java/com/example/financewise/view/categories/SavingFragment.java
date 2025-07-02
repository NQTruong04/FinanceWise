package com.example.financewise.view.categories;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.financewise.R;
import com.example.financewise.adapter.CategoryAdapter;
import com.example.financewise.databinding.FragmentSavingBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.viewmodel.CategoriesViewModel;
import com.example.financewise.viewmodel.SavingViewModel;

import java.util.ArrayList;
import java.util.List;

public class SavingFragment extends BaseFragment<FragmentSavingBinding, SavingViewModel> {
    private static final String TAG = "SavingFragment";
    private static final String ARG_USER_ID = "userId";
    private String userId;
    private CategoryAdapter savingAdapter;
    private NavigationManager navigationManager;

    public SavingFragment() {
        // Required empty public constructor
    }

    public static SavingFragment newInstance(String userId) {
        SavingFragment fragment = new SavingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentSavingBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSavingBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<SavingViewModel> getViewModelClass() {
        return SavingViewModel.class;
    }

    @Override
    protected void setupUI() {
        userId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : null;
        if(userId == null) {
            Log.e(TAG, "User ID is null, cannot setup UI");
            return;
        }
        viewModel.setUserId(userId);

        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);

        savingAdapter = new CategoryAdapter(saving ->{
            Bundle args = new Bundle();
            args.putString("categoryName", saving.getName());
            args.putString(ARG_USER_ID, userId);
            navigationManager.navigateWithSlideAnimation(DetailSavingFragment.newInstance(saving.getName(), userId), true);
        });
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvCategories.setAdapter(savingAdapter);

        binding.tvBackIcon.setOnClickListener(v ->{
            Bundle args = new Bundle();
            args.putString(ARG_USER_ID, userId);
            navigationManager.navigateWithSlideAnimation(CategoriesFragment.newInstance(userId), true);
        });
        binding.btnAddTransaction.setOnClickListener(v -> showAddSavingDialog());
        Log.d(TAG, "setupUI called with userId: " + userId);
    }

    @Override
    protected void observeViewModel() {
        if(savingAdapter == null) {
            Log.e(TAG, "SavingAdapter is null in observeViewModel, cannot observe data");
            return;
        }

        viewModel.getSavings().observe(getViewLifecycleOwner(), savings -> {
            Log.d(TAG, "Savings observed: " + (savings != null ? savings.size() : "null"));
            if (savings != null) {
                savingAdapter.submitList(savings);
            } else {
                Log.w(TAG, "Savings list is null, resetting adapter");
                savingAdapter.submitList(new ArrayList<>());
            }
        });
        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance ->{
            if(balance != null) {
                binding.tvTotalBalanceValue.setText(balance);
            } else {
                Log.w(TAG, "Total balance is null, not updating UI");
            }
        });
        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            if(expense != null) {
                binding.tvTotalExpenseValue.setText(expense);
            } else {
                Log.w(TAG, "Total expense is null, not updating UI");
            }
        });
        viewModel.getExpensePercentage().observe(getViewLifecycleOwner(), percentage -> {
            if(percentage != null) {
                binding.tvProgressPercentage.setText(formatCurrency(percentage.longValue()));
                binding.tvExpenseStatus.setText(getExpenseStatusText(percentage));
                updateProgressBarGuideline(percentage);
            } else {
                Log.w(TAG, "Expense percentage is null, not updating UI");
            }
        });

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

    private void updateProgressBarGuideline(float percentage) {
        if (percentage / 100 >= 0.18) {
            binding.guidelineEnd.setGuidelinePercent(percentage / 100f);
            if (percentage / 100 >= 0.75) {
                binding.llProgressBar.setBackgroundResource(R.drawable.rounded_red_progressbar_bg);
            } else {
                binding.llProgressBar.setBackgroundResource(R.drawable.rounded_black_progressbar_bg);
            }
        } else {
            binding.llProgressBar.setBackgroundResource(R.drawable.rounded_black_progressbar_bg);
            binding.guidelineEnd.setGuidelinePercent(0.18f);
        }
    }

    private void showAddSavingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Saving");

        // View cho dialog
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_saving, null);
        final TextView inputSavingName = dialogView.findViewById(R.id.tv_saving_name);
        final View btnSave = dialogView.findViewById(R.id.btn_save);
        final View btnCancel = dialogView.findViewById(R.id.btn_cancel);
        AlertDialog dialog  = builder.setView(dialogView).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnSave.setOnClickListener(v ->{
            String savingName = inputSavingName.getText().toString().trim();
            if(!savingName.isEmpty()) {
                addNewSaving(savingName);
                dialog.dismiss();
            } else {
                inputSavingName.setError("Saving name cannot be empty");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addNewSaving(String savingName) {
//        List<CategoriesViewModel.CategoryItem> currentSavings = viewModel.getSavings().getValue();
//        if (currentSavings != null) {
//            currentSavings.add(new SavingViewModel.CategoryItem(savingName, R.drawable.ic_savings)); // Sử dụng icon mặc định
//            viewModel.getSavings().setValue(new ArrayList<>(currentSavings));
//            Log.d(TAG, "Added new saving: " + savingName);
//        }
    }
    private String formatCurrency(long amount) {
        return String.format("$%,d", amount);
    }
}
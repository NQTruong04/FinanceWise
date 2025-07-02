package com.example.financewise.view.categories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;
import com.example.financewise.adapter.TransactionAdapter;
import com.example.financewise.databinding.FragmentDetailCategoryBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.view.transaction.AddTransactionFragment;
import com.example.financewise.viewmodel.DetailCategoryViewModel;

import java.util.ArrayList;

public class DetailCategoryFragment extends BaseFragment<FragmentDetailCategoryBinding, DetailCategoryViewModel> {
    private static final String TAG = "DetailCategoryFragment";
    private TransactionAdapter transactionAdapter;
    private NavigationManager navigationManager;
    private String userId;
    private String categoryName;

    public DetailCategoryFragment() {
        // Required empty public constructor
    }
    public static DetailCategoryFragment newInstance(String categoryName, String userId) {
        DetailCategoryFragment fragment = new DetailCategoryFragment();
        Bundle args = new Bundle();
        args.putString("categoryName", categoryName);
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentDetailCategoryBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentDetailCategoryBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<DetailCategoryViewModel> getViewModelClass() {
        return DetailCategoryViewModel.class;
    }

    @Override
    protected void setupUI() {
        userId = getArguments() != null ? getArguments().getString("userId") : null;
        categoryName = getArguments() != null ? getArguments().getString("categoryName") : null;
        if(userId == null || categoryName == null) {
            Log.e(TAG, "User ID or category name is null, cannot setup UI");
            return;
        }
        viewModel.setUserIdAndCategory(userId, categoryName);
        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);

        binding.tvCategoryName.setText(categoryName);
        transactionAdapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategories.setAdapter(transactionAdapter);

        binding.btnAddTransaction.setOnClickListener(v ->{
            navigationManager.navigateWithSlideAnimation(AddTransactionFragment.newInstance(userId), true);
        });

        binding.tvBackIcon.setOnClickListener(v ->{
            if(userId != null){
                navigationManager.navigateWithSlideAnimation(CategoriesFragment.newInstance(userId), true);
            }else {
                Log.e(TAG, "User ID is null, cannot navigate back to CategoriesFragment");
            }
        });
    }

    @Override
    protected void observeViewModel() {
        if(transactionAdapter == null){
            Log.e(TAG, "TransactionAdapter is null in observeViewModel");
            return;
        }
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions ->{
            Log.d(TAG, "Transactions updated: " + transactions.size() + " items");
            transactionAdapter.submitList(transactions);
        });
        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null && binding.tvTotalBalanceValue != null) {
                binding.tvTotalBalanceValue.setText(balance);
            }
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null && binding.tvTotalExpenseValue != null) {
                binding.tvTotalExpenseValue.setText(expense);
            }
        });

        viewModel.getExpensePercentage().observe(getViewLifecycleOwner(), percentage -> {
            if (percentage != null && binding.tvProgressPercentage != null && binding.tvExpenseStatus != null) {
                binding.tvProgressPercentage.setText(String.format("%.0f%%", percentage));
                binding.tvExpenseStatus.setText(getExpenseStatusText(percentage));
                updateProgressBarGuideline(percentage);
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
        if (binding == null || binding.guidelineEnd == null || binding.llProgressBar == null) {
            Log.e(TAG, "Binding or its views are null in updateProgressBarGuideline");
            return;
        }
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
}
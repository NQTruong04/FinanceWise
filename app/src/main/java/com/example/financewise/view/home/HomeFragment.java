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
    protected void setupUI() {
        String userId = getArguments() != null ? getArguments().getString("userId") : null;
        Log.d(TAG, "setupUI: userId = " + userId);
        if (userId == null) {
            Log.e(TAG, "userId is null, navigating back to login");
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_login);
            return;
        }

        viewModel.setUserId(userId);

        // Setup RecyclerView
        adapter = new TransactionAdapter(requireContext(), new ArrayList<>());
        binding.rvTransactionList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactionList.setAdapter(adapter);

        // Setup notification icon click
        binding.ivNotificationIcon.setOnClickListener(v -> {}
        );

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
            if (adapter != null) adapter.submitList(transactions);
        });

        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            Log.d(TAG, "Total Balance updated: " + balance);
            if (binding.tvTotalBalanceValue != null) binding.tvTotalBalanceValue.setText(balance);
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            Log.d(TAG, "Total Expense updated: " + expense);
            if (binding.tvTotalExpenseValue != null) binding.tvTotalExpenseValue.setText(expense);
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
}
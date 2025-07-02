package com.example.financewise.view.categories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.financewise.R;
import com.example.financewise.adapter.CategoryAdapter;
import com.example.financewise.databinding.FragmentCategoriesBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.viewmodel.CategoriesViewModel;

import java.util.ArrayList;

public class CategoriesFragment extends BaseFragment<FragmentCategoriesBinding, CategoriesViewModel> {
    private static final String TAG = "CategoriesFragment";
    private static final String ARG_USER_ID = "userId";
    private CategoryAdapter categoryAdapter;
    private NavigationManager navigationManager;

    public static CategoriesFragment newInstance(String userId) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentCategoriesBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        Log.d(TAG, "getViewBinding called");
        return FragmentCategoriesBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<CategoriesViewModel> getViewModelClass() {
        return CategoriesViewModel.class;
    }

    @Override
    protected void setupUI() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in setupUI, skipping UI initialization");
            return;
        }

        String userId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : null;
        if (userId == null) {
            Log.e(TAG, "User ID is null, cannot setup UI");
            return;
        }
        viewModel.setUserId(userId);

        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);

        // Khởi tạo adapter trước
        categoryAdapter = new CategoryAdapter(category -> {
            Bundle args = new Bundle();
            if(category.getName().equals("Savings")){
                args.putString(ARG_USER_ID, userId);
                navigationManager.navigateWithSlideAnimation(SavingFragment.newInstance(userId), true);
            }else{
                args.putString("categoryName", category.getName());
                args.putString(ARG_USER_ID, userId);
                navigationManager.navigateWithSlideAnimation(DetailCategoryFragment.newInstance(category.getName(), userId), true);
            }
        });
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvCategories.setAdapter(categoryAdapter);

        // Xử lý click tv_back_icon
        binding.tvBackIcon.setOnClickListener(v -> {
            Fragment homeFragment = HomeFragment.newInstance(userId);
            navigationManager.navigateWithSlideAnimation(HomeFragment.newInstance(userId), false);
        });

        Log.d(TAG, "UI setup completed, categoryAdapter: " + (categoryAdapter != null ? "not null" : "null"));
    }

    @Override
    protected void observeViewModel() {
        if (binding == null) {
            Log.e(TAG, "Binding is null in observeViewModel");
            return;
        }

        if (categoryAdapter == null) {
            Log.e(TAG, "categoryAdapter is null in observeViewModel");
            return;
        }

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            Log.d(TAG, "Categories observed, size: " + (categories != null ? categories.size() : 0));
            if (categories != null) {
                categoryAdapter.submitList(categories);
            } else {
                Log.e(TAG, "Categories list is null");
                categoryAdapter.submitList(new ArrayList<>());
            }
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
package com.example.financewise.view.categories;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;
import com.example.financewise.adapter.SavingAdapter;
import com.example.financewise.databinding.FragmentDetailSavingBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.viewmodel.DetailSavingViewModel;

import java.text.NumberFormat; // Thêm import này
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale; // Thêm import này

public class DetailSavingFragment extends BaseFragment<FragmentDetailSavingBinding, DetailSavingViewModel> {
    private static final String TAG = "DetailSavingFragment";
    private static final String ARG_SAVING_NAME = "savingName";
    private static final String ARG_USER_ID = "userId";
    private NavigationManager navigationManager;
    private SavingAdapter savingAdapter;
    private String userId;
    private String savingName;

    public DetailSavingFragment() {
        // Required empty public constructor
    }

    public static DetailSavingFragment newInstance(String savingName, String userId) {
        DetailSavingFragment fragment = new DetailSavingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SAVING_NAME, savingName); // Nên dùng hằng số
        args.putString(ARG_USER_ID, userId);       // Nên dùng hằng số
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentDetailSavingBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentDetailSavingBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<DetailSavingViewModel> getViewModelClass() {
        return DetailSavingViewModel.class;
    }

    @Override
    protected void setupUI() {
        userId = getArguments() != null ? getArguments().getString(ARG_USER_ID): null;
        savingName = getArguments() != null ? getArguments().getString(ARG_SAVING_NAME) : null;
        if(userId == null || savingName == null) {
            Log.e(TAG, "User ID or saving name is null, cannot setup UI");
            // Có thể hiển thị thông báo lỗi hoặc đóng Fragment tại đây
            return;
        }
        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);

        savingAdapter = new SavingAdapter(savingName);

        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategories.setAdapter(savingAdapter);

        binding.tvBackIcon.setOnClickListener(v -> {
            Log.d(TAG, "Back icon clicked");
            // Không cần Bundle args nếu SavingFragment không cần userId
            // args.putString("userId", userId);
            navigationManager.navigateWithSlideAnimation(SavingFragment.newInstance(userId), false); // Giả sử SavingFragment.newInstance(userId) là cách bạn chuyển
        });

        binding.btnAddSaving.setOnClickListener(v ->{
            Bundle args = new Bundle();
            args.putString(ARG_USER_ID, userId);
            // Có thể cần truyền thêm savingTargetId hoặc savingName nếu AddSavingFragment cần
            // (Bạn sẽ cần lấy currentTargetId từ ViewModel sau khi nó được tải)
            navigationManager.navigateWithSlideAnimation(AddSavingFragment.newInstance(userId), true);
        });
        Log.d(TAG, "User ID: " + userId + ", Saving Name: " + savingName);
        binding.calenderBtn.setOnClickListener(v -> showDatePickerDialog());
        viewModel.setUserIdAndSavingName(userId, savingName);
    }

    @Override
    protected void observeViewModel() {
        int iconRes = getIconForCategory(savingName);
        binding.ivTargetIcon.setImageResource(iconRes);
        viewModel.getSavingTarget().observe(getViewLifecycleOwner(), target -> {
            if(target != null){
                binding.tvGoal.setText(formatCurrency(target.getGoal()));
                binding.tvAmountSaved.setText(formatCurrency(target.getAmountSaved()));
                updateProgress(target.getAmountSaved(), target.getGoal());
                binding.tvCategoryName.setText(target.getCategory()); // Đặt tên category từ đối tượng target
            }else{
                binding.tvGoal.setText(formatCurrency(0.0)); // Truyền double 0.0
                binding.tvAmountSaved.setText(formatCurrency(0.0)); // Truyền double 0.0
                updateProgress(0, 0);
                binding.tvCategoryName.setText(savingName); // Giữ lại savingName nếu target không có
            }
        });
        viewModel.getSavings().observe(getViewLifecycleOwner(), savings -> {
            if(savings != null && !savings.isEmpty()){
                savingAdapter.submitList(savings);
                binding.rvCategories.setVisibility(View.VISIBLE);
                binding.tvEmptyView.setVisibility(View.GONE);
                Log.d(TAG, "Adapter submitted list with size: " + savings.size()); // Thêm log để xác nhận
            }else {
                savingAdapter.submitList(new ArrayList<>());
                binding.rvCategories.setVisibility(View.GONE);
                binding.tvEmptyView.setVisibility(View.VISIBLE);
                Log.d(TAG, "Adapter submitted empty list.");
            }
        });

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date ->{
            // Có thể cập nhật UI để hiển thị ngày đã chọn nếu cần
            // Ví dụ: binding.tvSelectedDate.setText(dateFormat.format(date));
        });
    }

    private void updateProgress(double amountSaved, double goal) {
        double percentage = (goal > 0) ? (amountSaved / goal) * 100 : 0;
        Log.d(TAG, "Updating progress: amountSaved = " + amountSaved + ", goal = " + goal + ", percentage = " + percentage);
        binding.tvProgressPercentage.setText(String.format(Locale.getDefault(), "%.0f%%", Math.min(percentage, 100)));
        binding.pbProgressCircle.setProgress((int) Math.min(percentage, 100));
        binding.tvTotalGoal.setText(formatCurrency(goal)); // Sử dụng phương thức formatCurrency mới
        binding.tvExpenseStatus.setText(getProgressStatusText(percentage));
        if (percentage / 100 >= 0.18) {
            binding.guidelineEnd.setGuidelinePercent((float) (percentage / 100f));
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

    private String getProgressStatusText(double percentage) {
        if (percentage < 50) {
            return "Less than 50% saved, keep going!";
        } else if (percentage < 80) {
            return "Over 50% saved, great progress!";
        } else {
            return "Over 80% saved, nearly there!";
        }
    }
    private int getIconForCategory(String category) {
        if (category == null) return R.drawable.ic_savings; // Xử lý trường hợp category là null
        switch (category.toLowerCase(Locale.getDefault())) { // Sử dụng Locale để chuyển đổi an toàn
            case "travel": return R.drawable.ic_travel_white;
            case "car": return R.drawable.ic_car_white;
            case "wedding": return R.drawable.ic_wedding_white;
            case "house": return R.drawable.ic_house_white;
            default: return R.drawable.ic_savings;
        }
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    viewModel.setSelectedDate(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Sửa phương thức formatCurrency để nhận double và định dạng đúng tiền tệ
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")); // Ví dụ: Tiếng Việt, Việt Nam
        formatter.setMaximumFractionDigits(0); // Không hiển thị số thập phân
        return formatter.format(amount);
    }
}
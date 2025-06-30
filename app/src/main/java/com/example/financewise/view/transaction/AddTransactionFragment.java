package com.example.financewise.view.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.financewise.R;
import com.example.financewise.data.model.Expense;
import com.example.financewise.data.model.Income;
import com.example.financewise.databinding.FragmentAddTransactionBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.viewmodel.AddTransactionViewModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionFragment extends BaseFragment<FragmentAddTransactionBinding, AddTransactionViewModel> {
    private static final String TAG = "AddTransactionFragment";
    private static final String ARG_USER_ID = "userId";
    private String userId;
    private Calendar selectedDate;
    private NavigationManager navigationManager;

    public static AddTransactionFragment newInstance(String userId){
        AddTransactionFragment fragment = new AddTransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public AddTransactionFragment(){

    }


    @Override
    protected FragmentAddTransactionBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentAddTransactionBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<AddTransactionViewModel> getViewModelClass() {
        return AddTransactionViewModel.class;
    }

    @Override
    protected void setupUI() {
        if(getArguments() != null){
            userId = getArguments().getString(ARG_USER_ID);
        }
        if (userId == null) {
            Log.e(TAG, "User ID is null in AddTransactionFragment, cannot proceed");
            Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.setUserId(userId);
        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);

        selectedDate = Calendar.getInstance();
        updateDateInView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.category_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategory.setAdapter(adapter);
        setupClickListeners();
    }

    private void setupClickListeners(){
        binding.tvBackIcon.setOnClickListener(v -> navigateBackToTransactionFragment());
        binding.llDatePicker.setOnClickListener(v ->showDatePickerDialog());
        binding.btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void updateDateInView(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        binding.tvSelectedDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void showDatePickerDialog(){
        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateInView();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveTransaction(){
        String amountStr = binding.etAmount.getText().toString().trim();
        String title = binding.etTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String category = binding.spCategory.getSelectedItem().toString();
        boolean isIncome = binding.rbIncome.isChecked();

        if(TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(title)){
            Toast.makeText(requireContext(), "Amount and Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isIncome) {
            amount = -Math.abs(amount);
        }

        Timestamp timestamp = new Timestamp(selectedDate.getTime());

        if(isIncome){
            Income income = new Income(userId, category, timestamp, amount, title, description);
            viewModel.addIncome(income);
        }else{
            Expense expense = new Expense(userId, category, timestamp, amount, title, description);
            viewModel.addExpense(expense);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel.getTransactionResult().observe(getViewLifecycleOwner(), success ->{
            if(success){
                Toast.makeText(requireContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                navigateBackToTransactionFragment();
            } else {
                Toast.makeText(requireContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void navigateBackToTransactionFragment(){
        navigationManager.navigateWithSlideAnimation(TransactionFragment.newInstance(userId), false);
    }
}
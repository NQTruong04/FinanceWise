package com.example.financewise.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.example.financewise.viewmodel.BaseViewModel;

public abstract class BaseFragment<VB extends ViewBinding, VM extends BaseViewModel> extends Fragment {
    private static final String TAG = "BaseFragment";
    protected VB binding;
    protected VM viewModel;

    protected abstract VB getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);
    protected abstract Class<VM> getViewModelClass();
    protected abstract void setupUI();
    protected abstract void observeViewModel();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        try {
            binding = getViewBinding(inflater, container);
            if (binding == null) {
                Log.e(TAG, "Binding is null after getViewBinding");
            } else {
                Log.d(TAG, "Binding initialized successfully: " + binding.getClass().getSimpleName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing binding: " + e.getMessage());
        }
        return binding != null ? binding.getRoot() : null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called, binding state: " + (binding != null ? "not null" : "null"));
        if (binding == null) {
            Log.e(TAG, "Binding is null in onViewCreated, skipping setup");
            return;
        }
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
        Log.d(TAG, "ViewModel initialized: " + viewModel.getClass().getSimpleName());
        setupUI();
        observeViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called, binding state: " + (binding != null ? "not null" : "null"));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called, binding state: " + (binding != null ? "not null" : "null"));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called, binding state: " + (binding != null ? "not null" : "null"));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called, binding state: " + (binding != null ? "not null" : "null"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called, binding state: " + (binding != null ? "not null" : "null"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called, binding state: " + (binding != null ? "not null" : "null"));
        binding = null;
        if (viewModel != null && !viewModel.isDisposed()) {
            Log.d(TAG, "Disposing ViewModel resources");
            viewModel.compositeDisposable.clear(); // Đảm bảo dispose nếu cần
        }
    }
}
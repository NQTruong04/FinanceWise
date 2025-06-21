package com.example.financewise.view.splash;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.MainActivity;
import com.example.financewise.R;
import com.example.financewise.databinding.FragmentLaunchBinding;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.viewmodel.LaunchViewModel;

public class LaunchFragment extends BaseFragment<FragmentLaunchBinding, LaunchViewModel> {
    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private static final String TAG = "LaunchFragment";

    public static LaunchFragment newInstance(){
        return new LaunchFragment();
    }

    @Override
    protected FragmentLaunchBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentLaunchBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<LaunchViewModel> getViewModelClass() {
        return LaunchViewModel.class;
    }

    @Override
    protected void setupUI() {
        Log.d(TAG, "setupUI: Starting splash delay");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Splash delay completed, calling onSplashComplete");
                if (viewModel != null) {
                    viewModel.onSplashComplete();
                } else {
                    Log.e(TAG, "ViewModel is null!");
                }
            }
        }, SPLASH_DELAY);
    }

    @Override
    protected void observeViewModel() {
        Log.d(TAG, "observeViewModel: Setting up observer");
        viewModel.getNavigateToLogin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null && aBoolean) {
                    navigateToLogin();
                }
            }
        });
    }
    private void navigateToLogin() {
        Log.d(TAG, "navigateToLogin called");
        if (getActivity() instanceof MainActivity) {
            Log.d(TAG, "Activity is MainActivity, calling navigateToLogin");
            ((MainActivity) getActivity()).navigateToLogin();
        } else {
            Log.e(TAG, "Activity is not MainActivity: " +
                    (getActivity() != null ? getActivity().getClass().getSimpleName() : "null"));
        }
    }
}
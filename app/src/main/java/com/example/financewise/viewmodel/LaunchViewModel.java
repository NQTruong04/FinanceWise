package com.example.financewise.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LaunchViewModel extends BaseViewModel {
    private static final String TAG = "LaunchViewModel";
    private final MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    public LiveData<Boolean> getNavigateToLogin() {
        return navigateToLogin;
    }
    public void onSplashComplete() {
        Log.d(TAG, "Splash screen completed, navigating to login");
        navigateToLogin.setValue(true);
    }
}

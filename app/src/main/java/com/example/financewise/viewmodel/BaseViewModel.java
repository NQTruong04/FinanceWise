package com.example.financewise.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import java.util.HashMap;
import java.util.Map;

public class BaseViewModel extends ViewModel {
    public final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        cleanupObservers();
    }

    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    protected <T> void observeForeverWithKey(String key, LiveData<T> liveData, Observer<T> observer, LifecycleOwner lifecycleOwner) {
        if (liveData != null && observer != null) {
            if (lifecycleOwner != null) {
                liveData.observe(lifecycleOwner, observer);
            } else {
                liveData.observeForever(observer);
            }
        }
    }

    protected void cleanupObservers() {
        // Default implementation does nothing; subclasses should override if needed
    }
    protected LiveData<?>[] getLiveDataInstances() {
        return new LiveData<?>[0]; // Override in subclasses if needed
    }
    public boolean isDisposed() {
        return compositeDisposable.isDisposed();
    }
}
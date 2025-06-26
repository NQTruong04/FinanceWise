package com.example.financewise.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Base ViewModel class providing common functionality for managing RxJava disposables and observers.
 */
public class BaseViewModel extends ViewModel {
    public final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Clears all disposables when the ViewModel is cleared.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        cleanupObservers();
    }

    /**
     * Returns the CompositeDisposable for adding or removing disposables.
     * @return CompositeDisposable instance
     */
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    /**
     * Registers an observer with a unique key for later cleanup (to be implemented by subclasses).
     * @param key Unique identifier for the observer
     * @param liveData The LiveData to observe
     * @param observer The observer to register
     * @param lifecycleOwner The LifecycleOwner to bind the observer to (null for manual cleanup)
     * @param <T> The type of data observed
     */
    protected <T> void observeForeverWithKey(String key, LiveData<T> liveData, Observer<T> observer, LifecycleOwner lifecycleOwner) {
        if (liveData != null && observer != null) {
            if (lifecycleOwner != null) {
                liveData.observe(lifecycleOwner, observer);
            } else {
                liveData.observeForever(observer);
            }
        }
    }

    /**
     * Cleans up all registered observers (to be implemented by subclasses).
     */
    protected void cleanupObservers() {
        // Default implementation does nothing; subclasses should override if needed
    }

    /**
     * Placeholder method to get all LiveData instances (to be overridden by subclasses if needed).
     * @return Array of LiveData instances
     */
    protected LiveData<?>[] getLiveDataInstances() {
        return new LiveData<?>[0]; // Override in subclasses if needed
    }
    public boolean isDisposed() {
        return compositeDisposable.isDisposed();
    }
}
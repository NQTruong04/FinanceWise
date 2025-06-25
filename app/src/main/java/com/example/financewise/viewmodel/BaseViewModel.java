package com.example.financewise.viewmodel;

import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    // Thêm phương thức để kiểm tra dispose
    public boolean isDisposed() {
        return compositeDisposable.isDisposed();
    }
}
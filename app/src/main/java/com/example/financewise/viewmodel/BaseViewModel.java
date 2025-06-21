package com.example.financewise.viewmodel;

import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}

package com.example.financewise.view.intro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;
import com.example.financewise.databinding.FragmentIntro1Binding;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.viewmodel.IntroViewModel;

public class Intro1Fragment extends BaseFragment<FragmentIntro1Binding, IntroViewModel> {
    public static Intro1Fragment newInstance() {
        return new Intro1Fragment();
    }

    @Override
    protected FragmentIntro1Binding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentIntro1Binding.inflate(inflater, container, false);
    }

    @Override
    protected Class<IntroViewModel> getViewModelClass() {
        return IntroViewModel.class;
    }

    @Override
    protected void setupUI() {

    }

    @Override
    protected void observeViewModel() {

    }
}
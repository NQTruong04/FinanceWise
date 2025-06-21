package com.example.financewise.view.intro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;
import com.example.financewise.databinding.FragmentIntro2Binding;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.viewmodel.IntroViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Intro2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Intro2Fragment extends BaseFragment<FragmentIntro2Binding, IntroViewModel> {
    public static Intro2Fragment newInstance(){
        return new Intro2Fragment();
    }

    @Override
    protected FragmentIntro2Binding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentIntro2Binding.inflate(inflater, container, false);
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
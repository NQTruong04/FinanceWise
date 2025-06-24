package com.example.financewise.view.categories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;

public class SavingFragment extends Fragment {

    public SavingFragment() {
        // Required empty public constructor
    }

    public static SavingFragment newInstance(String param1, String param2) {
        SavingFragment fragment = new SavingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saving, container, false);
    }
}
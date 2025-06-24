package com.example.financewise.view.categories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;

public class AddSavingFragment extends Fragment {

    public AddSavingFragment() {
        // Required empty public constructor
    }

    public static AddSavingFragment newInstance(String param1, String param2) {
        AddSavingFragment fragment = new AddSavingFragment();
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
        return inflater.inflate(R.layout.fragment_add_saving, container, false);
    }
}
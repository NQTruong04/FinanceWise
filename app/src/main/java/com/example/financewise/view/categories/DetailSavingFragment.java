package com.example.financewise.view.categories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;

public class DetailSavingFragment extends Fragment {

    public DetailSavingFragment() {
        // Required empty public constructor
    }

    public static DetailSavingFragment newInstance(String param1, String param2) {
        DetailSavingFragment fragment = new DetailSavingFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_detail_saving, container, false);
    }
}
package com.example.financewise.view.categories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;

public class DetailCategoryFragment extends Fragment {

    public DetailCategoryFragment() {
        // Required empty public constructor
    }
    public static DetailCategoryFragment newInstance(String param1, String param2) {
        DetailCategoryFragment fragment = new DetailCategoryFragment();
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
        return inflater.inflate(R.layout.fragment_detail_category, container, false);
    }
}
package com.example.financewise.view.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financewise.R;

public class PasswordSettingFragment extends Fragment {

    public PasswordSettingFragment() {
        // Required empty public constructor
    }
    public static PasswordSettingFragment newInstance(String param1, String param2) {
        PasswordSettingFragment fragment = new PasswordSettingFragment();
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
        return inflater.inflate(R.layout.fragment_password_setting, container, false);
    }
}
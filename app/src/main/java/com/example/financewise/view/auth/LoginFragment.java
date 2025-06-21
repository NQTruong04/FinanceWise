package com.example.financewise.view.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.financewise.R;
import com.example.financewise.databinding.FragmentLoginBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.viewmodel.LoginViewModel;

public class LoginFragment extends BaseFragment<FragmentLoginBinding, LoginViewModel> {
    private static final String TAG = "LoginFragment";
    private NavigationManager navigationManager;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    protected FragmentLoginBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<LoginViewModel> getViewModelClass() {
        return LoginViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);
        Log.d(TAG, "LoginFragment onViewCreated");
    }

    @Override
    protected void setupUI() {
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();
                viewModel.login(email, password);
            }
        });
        binding.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationManager.navigateTo(new SignUpFragment(), true);
            }
        });
        binding.signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationManager.navigateTo(new SignUpFragment(), true);
            }
        });
    }

    @Override
    protected void observeViewModel() {
        Log.d(TAG, "LoginFragment observeViewModel");
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if(loginResult != null){
                    if (loginResult.isSuccess()) {
                        Toast.makeText(getContext(), loginResult.getMessage(), Toast.LENGTH_SHORT).show();
                        navigationManager.navigateTo(new HomeFragment(), true);
                    } else {
                        Toast.makeText(getContext(), loginResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                binding.loaderView.setVisibility(!aBoolean ? View.VISIBLE : View.GONE);
                binding.loginButton.setEnabled(!aBoolean);
                // TODO: Show/hide loading indicator
            }
        });
    }
}
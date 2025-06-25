package com.example.financewise.view.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.financewise.R;
import com.example.financewise.databinding.FragmentSignUpBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.view.BaseFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.viewmodel.SignupViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpFragment extends BaseFragment<FragmentSignUpBinding, SignupViewModel> {
    private static final String TAG = "SignUpFragment";
    private NavigationManager navigationManager;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    protected FragmentSignUpBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSignUpBinding.inflate(inflater, container, false);
    }

    @Override
    protected Class<SignupViewModel> getViewModelClass() {
        return SignupViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationManager = new NavigationManager(requireActivity().getSupportFragmentManager(), R.id.fragment_container);
    }

    @Override
    protected void setupUI() {
        binding.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();
                String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();
                String name = binding.nameInput.getText().toString().trim();
                String phone = binding.phoneInput.getText().toString().trim();
                viewModel.signup(name, email, phone, password, confirmPassword);
            }
        });
        binding.loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationManager.navigateTo(new LoginFragment(), true);
            }
        });
    }

    @Override
    protected void observeViewModel() {
        viewModel.getSignupResult().observe(getViewLifecycleOwner(), new Observer<SignupResult>() {
            @Override
            public void onChanged(SignupResult signupResult) {
                if (signupResult != null) {
                    if (signupResult.isSuccess()) {
                        Toast.makeText(getContext(), signupResult.getMessage(), Toast.LENGTH_SHORT).show();
                        // Lấy userId từ FirebaseAuth
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            // Chuyển sang HomeFragment với userId
                            navigationManager.navigateTo(HomeFragment.newInstance(userId), false);
                        } else {
                            Toast.makeText(getContext(), "User not found after signup", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), signupResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                binding.loaderView.setVisibility(!aBoolean ? View.VISIBLE : View.GONE);
                binding.signButton.setEnabled(!aBoolean);
            }
        });
    }
}
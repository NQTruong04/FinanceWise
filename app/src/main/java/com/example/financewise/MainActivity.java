package com.example.financewise;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.financewise.utils.LoginCallBack;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financewise.databinding.ActivityMainBinding;
import com.example.financewise.navigation.NavigationManager;
import com.example.financewise.utils.PreferenceManager;
import com.example.financewise.view.auth.LoginFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.view.intro.Intro1Fragment;
import com.example.financewise.view.intro.Intro2Fragment;
import com.example.financewise.view.splash.LaunchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoginCallBack {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ViewPager2 viewPager;
    private IntroPageAdapter adapter;
    private NavigationManager navigationManager;
    private boolean isInLoginMode = false;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "MainActivity onCreate");

        // Khởi tạo NavigationManager
        viewPager = binding.viewPager;
        bottomNavigationView = binding.bnvBottomNavigation;
        bottomNavigationView.setVisibility(android.view.View.GONE); // Ẩn BottomNavigationView ban đầu
        navigationManager = new NavigationManager(getSupportFragmentManager(), R.id.fragment_container);

        if(PreferenceManager.getInstance(this).isLoggedIn()){
            navigateToHome();
        }else{
            initViews();
            setupViewPager();
        }

        setupBottomNavigation();
    }

    private void initViews() {
        // Đảm bảo fragment_container tồn tại
        if (findViewById(R.id.fragment_container) == null) {
            Log.e(TAG, "fragment_container not found in layout!");
        }
    }

    private void setupViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(Intro1Fragment.newInstance());
        fragments.add(Intro2Fragment.newInstance());
        fragments.add(LaunchFragment.newInstance());

        adapter = new IntroPageAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "Page selected: " + position);

                if (position == 2) { // LaunchFragment position
                    // Disable swiping on launch fragment
                    viewPager.setUserInputEnabled(false);
                    Log.d(TAG, "Hidden TabLayout and disabled swiping on LaunchFragment");
                }else{
                    // Enable swiping on other fragments
                }
            }
        });
    }

    public void setupBottomNavigation(){
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.nav_home){
                navigationManager.navigateTo(new HomeFragment(), true);
                return true;
            } else if (itemId  == R.id.nav_analysis) {
                return true;
            }else if(itemId == R.id.nav_transaction) {
                return true;
            }else if (itemId == R.id.nav_category) {
                return true;
            }else if (itemId == R.id.nav_profile){
                return true;
            }
            return false;
        });
    }

    public void navigateToLogin() {
        Log.d(TAG, "navigateToLogin called");
        if (isInLoginMode) {
            Log.d(TAG, "Already in login mode, ignoring");
            return;
        }
        isInLoginMode = true;
        // Ẩn ViewPager và hiển thị fragment container
        viewPager.setVisibility(android.view.View.GONE);
        findViewById(R.id.fragment_container).setVisibility(android.view.View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE); // Ẩn BottomNavigationView
        // Navigate to LoginFragment
        navigationManager.navigateTo(LoginFragment.newInstance(), false);
        Log.d(TAG, "Successfully navigated to LoginFragment");
    }

    public void navigateToHome(){
        isInLoginMode = true;
        viewPager.setVisibility(android.view.View.GONE);
        findViewById(R.id.fragment_container).setVisibility(android.view.View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE); // Hiển thị BottomNavigationView
        Log.d(TAG, "Navigating to Home, BottomNavigationView set to VISIBLE");
        navigationManager.navigateTo(new HomeFragment(), false);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
    @Override
    public void onBackPressed() {
        if (isInLoginMode) {
            // Nếu đang ở LoginFragment, không cho phép back
            // Hoặc bạn có thể thoát app
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess called");
        navigateToHome();
    }

    private static class IntroPageAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments;

        public IntroPageAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}
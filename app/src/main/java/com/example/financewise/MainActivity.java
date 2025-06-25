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
import com.example.financewise.view.analysis.AnalysisFragment;
import com.example.financewise.view.auth.LoginFragment;
import com.example.financewise.view.categories.CategoriesFragment;
import com.example.financewise.view.home.HomeFragment;
import com.example.financewise.view.intro.Intro1Fragment;
import com.example.financewise.view.intro.Intro2Fragment;
import com.example.financewise.view.profile.ProfileFragment;
import com.example.financewise.view.splash.LaunchFragment;
import com.example.financewise.view.transaction.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private String currentUserId;
    private boolean isNavigating = false;

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
        bottomNavigationView.setVisibility(View.GONE);
        navigationManager = new NavigationManager(getSupportFragmentManager(), R.id.fragment_container);

        // Lấy userId từ Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user != null ? user.getUid() : null;
        PreferenceManager.getInstance(this).setLoggedIn(currentUserId != null);

        // Kiểm tra trạng thái lần đầu chạy
        if (PreferenceManager.getInstance(this).isFirstTime()) {
            setupViewPager();
        } else if (currentUserId != null && PreferenceManager.getInstance(this).isLoggedIn()) {
            navigateToHome();
        } else {
            navigateToLogin();
        }

        setupBottomNavigation();
    }

    private void initViews() {
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
                    viewPager.setUserInputEnabled(false);
                    Log.d(TAG, "Hidden TabLayout and disabled swiping on LaunchFragment");
                    navigateToLogin(); // Chuyển sang Login sau LaunchFragment
                }
            }
        });
    }

    public void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(isNavigating) return true;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                navigateToHome();
                return true;
            } else if (itemId == R.id.nav_analysis) {
                navigationManager.navigateTo(new AnalysisFragment(), true);
                return true;
            } else if (itemId == R.id.nav_transaction) {
                navigationManager.navigateTo(new TransactionFragment(), true);
                return true;
            } else if (itemId == R.id.nav_category) {
                navigationManager.navigateTo(new CategoriesFragment(), true);
                return true;
            } else if (itemId == R.id.nav_profile) {
                navigationManager.navigateTo(new ProfileFragment(), true);
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
        viewPager.setVisibility(View.GONE);
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
        navigationManager.navigateTo(LoginFragment.newInstance(), false);
        Log.d(TAG, "Successfully navigated to LoginFragment");
    }

    public void navigateToHome() {
        if(isNavigating) return;
        isNavigating = true;
        try{
            isInLoginMode = true;
            viewPager.setVisibility(View.GONE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Navigating to Home, BottomNavigationView set to VISIBLE, userId: " + currentUserId);
            navigationManager.navigateTo(HomeFragment.newInstance(currentUserId), false);
            // Không gọi setSelectedItemId để tránh vòng lặp
        }finally {
            isNavigating = false;
        }
        PreferenceManager.getInstance(this).setFirstTime(true); // Đánh dấu không phải lần đầu
    }

    @Override
    public void onBackPressed() {
        if (isInLoginMode) {
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess called");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user != null ? user.getUid() : null;
        PreferenceManager.getInstance(this).setLoggedIn(currentUserId != null);
        if (currentUserId != null) {
            navigateToHome();
        } else {
            Log.e(TAG, "UserId is null after login");
            navigateToLogin(); // Fallback nếu login thất bại
        }
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
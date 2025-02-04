package com.example.recipes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.recipes.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private final int splashScreenTime = 1000; // 3 seconds
    private final int timeInterval = 100; // 0.1 seconds
    private int progress = 0; // 0 to 100 for progress bar
    private Runnable runnable;
    private Handler handler;

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater()); // View Binding for Splash Screen
        setContentView(binding.getRoot());
        binding.progressBar.setMax(splashScreenTime); // set max value for progress bar
        binding.progressBar.setProgress(progress); // set initial value for progress bar
        handler = new Handler(Looper.getMainLooper()); // create handler
        runnable = () -> {
            if (progress < splashScreenTime) {
                progress += timeInterval;
                binding.progressBar.setProgress(progress);
                handler.postDelayed(runnable, timeInterval);
            } else {
                FirebaseApp.initializeApp(this);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                startActivity(user != null ? new Intent(SplashActivity.this, MainActivity.class) : new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        };
        handler.postDelayed(runnable, timeInterval);
    }
}
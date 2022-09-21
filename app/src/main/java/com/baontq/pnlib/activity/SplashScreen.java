package com.baontq.pnlib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baontq.pnlib.MainActivity;
import com.baontq.pnlib.R;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSharedPreferences("session", Context.MODE_PRIVATE).getBoolean(getString(R.string.session_is_remember), false)) {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }
        finish();
    }
}

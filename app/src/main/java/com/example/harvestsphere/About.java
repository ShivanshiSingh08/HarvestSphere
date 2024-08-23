package com.example.harvestsphere;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView aboutTextView = findViewById(R.id.aboutTextView);

        if (logoImageView != null && aboutTextView != null) {

        } else {
            Log.e("AboutActivity", "Some views are null!");
        }
    }
}
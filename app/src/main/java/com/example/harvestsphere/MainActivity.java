package com.example.harvestsphere;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_PICK = 102;

    private ImageView mPhotoImageView; // Declare ImageView variable
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(findViewById(R.id.toolbar)); // Set up the ActionBar with Toolbar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable Up button for ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable default title display

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.app_name); // Set custom title

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });

        // Ensure the view with ID 'mPhotoImageView' is found
        mPhotoImageView = findViewById(R.id.mPhotoImageView);

        // Set onClickListener for Camera Button
        findViewById(R.id.mCameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Set onClickListener for Gallery Button
        findViewById(R.id.mGalleryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private void handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_login) {
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();
            // Add your login handling code here
        } else if (id == R.id.nav_home) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            // Add your home navigation handling code here
        } else if (id == R.id.nav_weather) {
            startActivity(new Intent(MainActivity.this, Weather_ForeCast.class));
        } else if (id == R.id.nav_sell) {
            Toast.makeText(this, "Selling Crops clicked", Toast.LENGTH_SHORT).show();
            // Add your selling crops navigation handling code here
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
            // Add your about navigation handling code here
        } else if (id == R.id.logout) {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            // Add your logout handling code here
        } else {
            Toast.makeText(this, "Unknown item clicked", Toast.LENGTH_SHORT).show();
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mPhotoImageView.setImageBitmap(imageBitmap);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                mPhotoImageView.setImageURI(data.getData());
            }
        }
    }
}

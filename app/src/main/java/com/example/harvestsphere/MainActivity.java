package com.example.harvestsphere;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_PICK = 102;
    private static final int mCameraRequestCode = 0;
    private static final int mGalleryRequestCode = 2;

    private ImageView mPhotoImageView;
    private Classifier mClassifier;
    private Bitmap mBitmap;

    private static final int mInputSize = 224;
    private static final String mModelPath = "resnet50_latest.tflite";
    private static final String mLabelPath = "plant_labels.txt";
    private static final String mSamplePath = "soybean.JPG";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        AssetManager assetManager = getAssets();

        try {
            mClassifier = new Classifier(assetManager, mModelPath, mLabelPath, mInputSize);

            mBitmap = BitmapFactory.decodeStream(getResources().getAssets().open(mSamplePath));
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true);
            mPhotoImageView = findViewById(R.id.mPhotoImageView);
            mPhotoImageView.setImageBitmap(mBitmap);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading assets or model", Toast.LENGTH_LONG).show();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(R.string.app_name);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });

        findViewById(R.id.mCameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.mGalleryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        findViewById(R.id.mDetectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClassifier != null && mBitmap != null) {
                    List<Classifier.Recognition> results = mClassifier.recognizeImage(mBitmap);
                    if (results != null && !results.isEmpty()) {
                        Classifier.Recognition result = results.get(0);
                        String resultText = result.getTitle() + "\n Confidence: " + result.getConfidence();
                        TextView resultTextView = findViewById(R.id.mResultTextView);
                        resultTextView.setText(resultText);
                    } else {
                        Toast.makeText(MainActivity.this, "No recognition results found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Classifier or Bitmap is not initialized", Toast.LENGTH_SHORT).show();
                }
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
            startActivity(new Intent(MainActivity.this, SendOTPActivity.class));
            // Add your login handling code here
        } else if (id == R.id.nav_home) {
            Toast.makeText(this, "Already in home page", Toast.LENGTH_SHORT).show();
            // Add your home navigation handling code here
        } else if (id == R.id.nav_weather) {
            startActivity(new Intent(MainActivity.this, Weather_ForeCast.class));
        } else if (id == R.id.nav_sell) {
            startActivity(new Intent(MainActivity.this, SellProducts.class));
            // Add your selling crops navigation handling code here
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, About.class));
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
                    mBitmap = scaleImage(imageBitmap);
                    mPhotoImageView.setImageBitmap(mBitmap);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    mBitmap = scaleImage(mBitmap);
                    mPhotoImageView.setImageBitmap(mBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image from gallery", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public Bitmap scaleImage(Bitmap bitmap) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float scaleWidth = (float) mInputSize / originalWidth;
        float scaleHeight = (float) mInputSize / originalHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true);
    }
}
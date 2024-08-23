package com.example.harvestsphere;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvestsphere.adapter.ProductAdapter;
import com.example.harvestsphere.model.CropModel;
import com.example.harvestsphere.model.FertilizerModel;
import com.example.harvestsphere.model.PesticideModel;
import com.example.harvestsphere.model.ToolModel;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SellProducts extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Object> productList;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sell_products);

        // Initialize DrawerLayout and Toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });

        // Initialize RecyclerView
        productRecyclerView = findViewById(R.id.product_recycler_view);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load data and set adapter
        productList = new ArrayList<>();
        loadJsonData();
        productAdapter = new ProductAdapter(productList, this);
        productRecyclerView.setAdapter(productAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_login) {
            startActivity(new Intent(SellProducts.this, SendOTPActivity.class));
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(SellProducts.this, MainActivity.class));
        } else if (id == R.id.nav_weather) {
            startActivity(new Intent(SellProducts.this, Weather_ForeCast.class));
        } else if (id == R.id.nav_sell) {
            Toast.makeText(this, "Already in Sell Products", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(SellProducts.this, About.class));
        } else if (id == R.id.logout) {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unknown item clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJsonData() {
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            JSONArray cropsArray = jsonObject.getJSONArray("crops");
            for (int i = 0; i < cropsArray.length(); i++) {
                JSONObject cropObject = cropsArray.getJSONObject(i);
                CropModel crop = new CropModel(
                        cropObject.getString("name"),
                        cropObject.getString("season"),
                        cropObject.getDouble("pricePerKg"),
                        cropObject.getString("image"),
                        cropObject.getString("soilType"),
                        cropObject.getString("description")
                );
                productList.add(crop);
            }

            JSONArray fertilizersArray = jsonObject.getJSONArray("fertilizers");
            for (int i = 0; i < fertilizersArray.length(); i++) {
                JSONObject fertilizerObject = fertilizersArray.getJSONObject(i);
                List<String> descriptionList = Arrays.asList(fertilizerObject.getString("description"));
                FertilizerModel fertilizer = new FertilizerModel(
                        fertilizerObject.getString("name"),
                        fertilizerObject.getDouble("pricePerKg"),
                        fertilizerObject.getString("image"),
                        descriptionList,
                        "SomeOtherString"
                );
                productList.add(fertilizer);
            }

            JSONArray pesticidesArray = jsonObject.getJSONArray("pesticides");
            for (int i = 0; i < pesticidesArray.length(); i++) {
                JSONObject pesticideObject = pesticidesArray.getJSONObject(i);
                List<String> descriptionList = Arrays.asList(pesticideObject.getString("description"));
                PesticideModel pesticide = new PesticideModel(
                        pesticideObject.getString("name"),
                        pesticideObject.getDouble("pricePerLiter"),
                        pesticideObject.getString("image"),
                        descriptionList,
                        "SomeOtherString"
                );
                productList.add(pesticide);
            }

            JSONArray toolsArray = jsonObject.getJSONArray("tools");
            for (int i = 0; i < toolsArray.length(); i++) {
                JSONObject toolObject = toolsArray.getJSONObject(i);
                ToolModel tool = new ToolModel(
                        toolObject.getString("name"),
                        toolObject.getDouble("price"),
                        toolObject.getString("image"),
                        toolObject.getString("description")
                );
                productList.add(tool);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading JSON data", Toast.LENGTH_LONG).show();
        }
    }
}

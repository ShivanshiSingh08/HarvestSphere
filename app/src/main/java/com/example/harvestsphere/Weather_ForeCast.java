package com.example.harvestsphere;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Weather_ForeCast extends AppCompatActivity {

    private static final String TAG = "Weather_ForeCast";
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView CityNameTV, TempTV, ConditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV, searchIV, iconIV;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private static final int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_weather_fore_cast);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeLocationManager();

        searchIV.setOnClickListener(v -> {
            String city = cityEdt.getText().toString().trim();
            if (city.isEmpty()) {
                Toast.makeText(Weather_ForeCast.this, "Please enter city name", Toast.LENGTH_SHORT).show();
            } else {
                cityName = city;
                getWeatherInfo(cityName);
            }
        });
    }

    private void initializeViews() {
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        CityNameTV = findViewById(R.id.idTVCityName);
        TempTV = findViewById(R.id.idTVTemperature);
        ConditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        searchIV = findViewById(R.id.idIVSearch);
        iconIV = findViewById(R.id.idIVIcon);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
    }

    private void initializeLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            getLocationAndWeatherInfo();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndWeatherInfo() {
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLatitude(), location.getLongitude());
            if (!cityName.equals("Not Found")) {
                getWeatherInfo(cityName);
            } else {
                Toast.makeText(this, "Unable to find city from location.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=8d54d0074d6c49d7845132203242507&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        CityNameTV.setText(cityName);
        loadingPB.setVisibility(View.VISIBLE);
        homeRL.setVisibility(View.GONE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingPB.setVisibility(View.GONE);
                    homeRL.setVisibility(View.VISIBLE);
                    weatherRVModalArrayList.clear();
                    try {
                        String temperature = response.getJSONObject("current").getString("temp_c");
                        TempTV.setText(temperature + "°C");
                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                        ConditionTV.setText(condition);

                        int isDay = response.getJSONObject("current").getInt("is_day");
                        String backgroundUrl = isDay == 1 ?
                                "https://c8.alamy.com/comp/ET16FW/view-of-early-morning-clouds-india-ET16FW.jpg" :
                                "https://i.pinimg.com/736x/3b/6e/b4/3b6eb49be0fda397ed77d98f108320d2.jpg";
                        Picasso.get().load(backgroundUrl).into(backIV);

                        JSONObject forecastObj = response.getJSONObject("forecast");
                        JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                        JSONArray hourArray = forecast0.getJSONArray("hour");

                        for (int i = 0; i < hourArray.length(); i++) {
                            JSONObject hourObj = hourArray.getJSONObject(i);
                            String time = hourObj.getString("time");
                            String temp = hourObj.getString("temp_c");
                            String img = hourObj.getJSONObject("condition").getString("icon");
                            String wind = hourObj.getString("wind_kph");
                            weatherRVModalArrayList.add(new WeatherRVModal(time, temp, img, wind));
                        }
                        weatherRVAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to parse weather data", e);
                        Toast.makeText(Weather_ForeCast.this, "Failed to parse weather data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching weather data", error);
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(Weather_ForeCast.this, "Failed to fetch weather data. Check the logs for details.", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
    }


    private String getCityName(double latitude, double longitude) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                cityName = address.getLocality();
                if (cityName == null || cityName.isEmpty()) {
                    cityName = "Not Found";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Geocoding error", e);
        }
        return cityName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndWeatherInfo();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

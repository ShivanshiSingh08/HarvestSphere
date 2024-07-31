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

import androidx.activity.EdgeToEdge;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Weather_ForeCast extends AppCompatActivity {

    private static final String TAG = "Weather_ForeCast";
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView CityNameTV, TempTV, ConditionTV, cropSuggestionsTV, cropListTV;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_fore_cast);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        CityNameTV = findViewById(R.id.idTVCityName);
        TempTV = findViewById(R.id.idTVTemperature);
        ConditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cropSuggestionsTV = findViewById(R.id.idTVCropSuggestions);
        cropListTV = findViewById(R.id.idTVCropList);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        searchIV = findViewById(R.id.idIVSearch);
        iconIV = findViewById(R.id.idIVIcon);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Weather_ForeCast.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            getLocationAndWeatherInfo();
        }

        searchIV.setOnClickListener(v -> {
            String city = cityEdt.getText().toString();
            if (city.isEmpty()) {
                Toast.makeText(Weather_ForeCast.this, "Please enter city name", Toast.LENGTH_SHORT).show();
            } else {
                cityName = city;
                getWeatherInfo(city);
            }
        });
    }

    private void getLocationAndWeatherInfo() {
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
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
                        // Log the entire response for debugging
                        Log.d(TAG, "Weather API Response: " + response.toString());

                        String temperature = response.getJSONObject("current").getString("temp_c");
                        TempTV.setText(temperature + "°C");

                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                        Log.d(TAG, "Weather Condition: " + condition);

                        // Update the icon based on condition
                        Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                        ConditionTV.setText(condition);

                        int isDay = response.getJSONObject("current").getInt("is_day");
                        String backgroundUrl = isDay == 1 ?
                                "https://img.freepik.com/free-photo/beautiful-sunset-sky_1150-5359.jpg?w=996&t=st=1722433262~exp=1722433862~hmac=65f1bf41d3a1888a65a8d6a7748437c9a62f186103409642266eefd551eee821" :
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

                        suggestCrops(temperature, condition);

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
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cache-Control", "no-cache");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
    private void suggestCrops(String temperature, String condition) {
        String cropSuggestion = "";
        double temp = Double.parseDouble(temperature);

        condition = condition.toLowerCase(); // Convert to lowercase for easier matching

        if (condition.contains("light rain") || condition.contains("patchy rain") || condition.contains("drizzle")) {
            if (temp > 20) {
                cropSuggestion = "Pulses, Vegetables"; // Crops suitable for light rain and warm temperatures
            } else {
                cropSuggestion = "Barley, Oats"; // Crops suitable for light rain and cooler temperatures
            }
        } else if (condition.contains("moderate rain") || condition.contains("heavy rain") || condition.contains("showers")) {
            if (temp > 20) {
                cropSuggestion = "Rice, Sugarcane"; // Crops suitable for heavy rain and warm temperatures
            } else {
                cropSuggestion = "Wheat, Barley"; // Crops suitable for heavy rain and cooler temperatures
            }
        } else if (condition.contains("sunny") || condition.contains("clear") || condition.contains("mostly sunny")) {
            if (temp > 25) {
                cropSuggestion = "Cotton, Millets"; // Crops suitable for hot and sunny weather
            } else {
                cropSuggestion = "Pulses, Vegetables"; // Crops suitable for mild and sunny weather
            }
        } else if (condition.contains("cloudy") || condition.contains("overcast") || condition.contains("partly cloudy")) {
            if (temp > 20) {
                cropSuggestion = "Maize, Sugarcane"; // Crops suitable for cloudy and warm conditions
            } else {
                cropSuggestion = "Peas, Mustard"; // Crops suitable for cloudy and cooler conditions
            }
        } else if (condition.contains("haze") || condition.contains("fog") || condition.contains("mist")) {
            cropSuggestion = "Winter Vegetables, Mustard"; // Crops suitable for reduced sunlight and cooler temperatures
        } else if (condition.contains("storm") || condition.contains("thunder")) {
            cropSuggestion = "Greens, Herbs"; // Short-duration crops for unpredictable weather
        } else {
            cropSuggestion = "Pulses, Vegetables"; // Default crops for unclassified weather conditions
        }

        cropListTV.setText(cropSuggestion);
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        break;
                    } else {
                        Log.d(TAG, "CITY NOT FOUND");
                        Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                finish();
            }
        }
    }
}

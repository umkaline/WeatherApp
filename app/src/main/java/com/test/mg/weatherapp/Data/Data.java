package com.test.mg.weatherapp.Data;

import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by umka on 19.11.15.
 */
public class Data {
    private String city;
    private String weatherDescription;
    private String iconCode;
    private double temperature;
    private double pressure;
    private double longitude;
    private double latitude;
    private int humidity;
    private long sunRiseTime;
    private long sunSetTime;
    private double windSpeed;
    private float windDegree;

    public Data(JSONObject json) {
        try {
            weatherDescription = json.optJSONArray("weather").getJSONObject(0).getString("description");
            iconCode = json.optJSONArray("weather").getJSONObject(0).getString("icon");
            temperature = json.optJSONObject("main").optDouble("temp");
            pressure = json.optJSONObject("main").optDouble("pressure");
            humidity = json.optJSONObject("main").optInt("humidity");
            windSpeed = json.optJSONObject("wind").optInt("speed");
            windDegree = json.optJSONObject("wind").optInt("deg");
            longitude = json.optJSONObject("coord").optDouble("lon");
            latitude = json.optJSONObject("coord").optDouble("lat");
            city = json.getString("name");
            city = city + ", " + json.optJSONObject("sys").optString("country");
            sunRiseTime = json.optJSONObject("sys").optLong("sunrise");
            sunSetTime = json.optJSONObject("sys").optLong("sunset");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getWeatherDescription() {
        return weatherDescription;
    }


    public int getTemperature() {
        return (int)temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public float getWindDegree() {
        return windDegree;
    }

    public long getSunRiseTime() {
        return sunRiseTime;
    }

    public long getSunSetTime() {
        return sunSetTime;
    }

    public String getIconCode() {
        return iconCode;
    }

    public String getCity() {
        return city;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}

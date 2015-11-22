package com.test.mg.weatherapp.Service;

import android.graphics.drawable.Drawable;

import com.test.mg.weatherapp.Data.Data;

/**
 * Created by umka on 19.11.15.
 */
public interface WeatherServiceCallback {
    void serviceSucces (Data data);
    void serviceFail (Exception exception);

    void updateIcon(Drawable icon);
}

package com.test.mg.weatherapp.Service;

import android.graphics.drawable.Drawable;

/**
 * Created by umka on 20.11.15.
 */
public interface MapsServiceCallback {
    void serviceSucces (Drawable mapImage);
    void serviceFail (Exception exception);


}

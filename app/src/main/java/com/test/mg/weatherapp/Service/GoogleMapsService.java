package com.test.mg.weatherapp.Service;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import com.test.mg.weatherapp.MainActivity;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by umka on 20.11.15.
 */
public class GoogleMapsService {
    private static final String apiKey = "AIzaSyBVQT211kP7X4GKsUI1-KeC2asjTMUBlck";

    private MapsServiceCallback callback;
    private String location;

    private Exception error;

    public GoogleMapsService(MapsServiceCallback callback) {
        this.callback = callback;
    }

    public void getCityLocationImage() {
        new AsyncTask<String, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(String... params) {
                try {
                    int height = ((MainActivity)callback).getDisplayHeight();
                    int width = ((MainActivity)callback).getDisplayWidth();
                    String url = String.format("https://maps.googleapis.com/maps/api/staticmap?center=%s&maptype=hybrid&size=%s&markers=%s&key=%s",
                            Uri.encode(params[0]), Uri.encode(width + "x" + height), Uri.encode(params[0]), Uri.encode(apiKey));
                    InputStream is = (InputStream) new URL(url).getContent();
                    Drawable d = Drawable.createFromStream(is, "imageView");
                    return d;
                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                if (error == null && drawable != null) {
                    callback.serviceSucces(drawable);
                } else if (error != null) {
                    callback.serviceFail(error);
                }
            }
        }.execute(location);

    }

    public void setLocation(String location) {
        this.location = location;
    }
}

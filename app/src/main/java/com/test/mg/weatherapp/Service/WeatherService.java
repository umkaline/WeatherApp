package com.test.mg.weatherapp.Service;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import com.test.mg.weatherapp.Data.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by umka on 19.11.15.
 */
public class WeatherService {
    private static final String apiKey = "639f57bde1f53d836de7615846d2fb81";

    private WeatherServiceCallback callback;
    private String location;

    private Exception error;

    public WeatherService(WeatherServiceCallback callback) {
        this.callback = callback;
    }

    public void getWeatherData () {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String address = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s",
                        Uri.encode(location), Uri.encode(apiKey));

                try {
                    URL url = new URL(address);
                    URLConnection connection = url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder result = new StringBuilder("");
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return result.toString();

                } catch (MalformedURLException e) {
                    error = e;
                } catch (IOException e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {

                if (error == null) {
                    try {
                        JSONObject json = new JSONObject(s);

                        int responseCode = json.optInt("cod");
                        if(responseCode == 404) {
                            callback.serviceFail(new Exception("No such city"));
                            return;
                        }

                        final Data data = new Data(json);

                        callback.serviceSucces(data);

                    } catch (JSONException e) {
                        callback.serviceFail(error);
                    }
                } else {
                    callback.serviceFail(error);
                }
            }
        }.execute(location); {

        }
    }

    public void getIcon (final Data data) {

        new AsyncTask<Data, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Data... params) {
                try {
                    String url = String.format("http://api.openweathermap.org/img/w/%s.png", Uri.encode(data.getIconCode()));
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
                    callback.updateIcon(drawable);
                } else if (error != null) {
                    callback.serviceFail(error);
                }
            }
        }.execute(data);

    }

    public void setLocation(String location) {
        this.location = location;
    }
}

package com.test.mg.weatherapp;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.mg.weatherapp.Data.Data;
import com.test.mg.weatherapp.Service.GoogleMapsService;
import com.test.mg.weatherapp.Service.MapsServiceCallback;
import com.test.mg.weatherapp.Service.WeatherServiceCallback;
import com.test.mg.weatherapp.Service.WeatherService;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements WeatherServiceCallback, MapsServiceCallback {

    private String location = "Kiev";
    private int displayWidth, displayHeight;

    private EditText cityEditText;
    private ImageView conditionImageView;
    private ImageView mapImageView;
    private ImageView windArrowImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView cityTextView;
    private TextView windSpeedTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView sunRiseTextView;
    private TextView sunSetTextView;
    private TextView coordinatesTextView;

    private GoogleMapsService mapsService;
    private WeatherService weatherService;
    private ProgressDialog progressDialog;

    private Data data;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onDestroy();
        onCreate(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapsService = new GoogleMapsService(this);
        weatherService = new WeatherService(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.action_bar);

        hideKeyBoard();

        initializeViews(actionBar);

        //noinspection ResourceType
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayWidth = metrics.widthPixels;
        displayHeight = metrics.heightPixels;

        cityEditText.setText(location);

        weatherService.setLocation(location);
        weatherService.getWeatherData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

    }

    private void hideKeyBoard() {
        cityEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    getWeather_Click(null);
                    return true;
                }
                return false;
            }
        });
    }

    private void initializeViews(android.support.v7.app.ActionBar actionBar) {
        conditionImageView = (ImageView)findViewById(R.id.conditionImageView);
        mapImageView = (ImageView)findViewById(R.id.mapImageView);
        windArrowImageView = (ImageView)findViewById(R.id.windArrowimageView);
        temperatureTextView = (TextView)findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView)findViewById(R.id.conditionTextView);
        cityTextView = (TextView)findViewById(R.id.cityTextView);
        humidityTextView = (TextView)findViewById(R.id.humidityTextView);
        windSpeedTextView = (TextView)findViewById(R.id.windSpeedTextView);
        pressureTextView = (TextView)findViewById(R.id.pressureTextView);
        sunRiseTextView = (TextView)findViewById(R.id.sunRiseTextView);
        sunSetTextView = (TextView)findViewById(R.id.sunSetTextView);
        coordinatesTextView = (TextView)findViewById(R.id.coordinatesTextView);
        cityEditText = (EditText)actionBar.getCustomView().findViewById(R.id.cityEditText);

    }

    @Override
    public void serviceSucces(Data data) {

        this.data = data;

        cityTextView.setText(data.getCity());
        windSpeedTextView.setText("Wind: " + (int)data.getWindSpeed() + " m/s");
        humidityTextView.setText("Humidity: " + data.getHumidity() + "%");
        pressureTextView.setText("Pressure: " + (int)data.getPressure() + " mbar");
        temperatureTextView.setText(String.valueOf(data.getTemperature()) + " \u00b0C");
        conditionTextView.setText(data.getWeatherDescription());
        coordinatesTextView.setText(data.getLatitude() + ", " + data.getLongitude());

        setSunRiseAndSunSetTimes(data);
        rotateAndSetWindArrow(data);

        mapsService.setLocation(data.getCity());
        mapsService.getCityLocationImage();

        weatherService.getIcon(data);
    }

    private void setSunRiseAndSunSetTimes(Data data) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        calendar.setTimeInMillis(data.getSunRiseTime() * 1000);
        sunRiseTextView.setText(sdf.format(calendar.getTime()));
        calendar.setTimeInMillis(data.getSunSetTime() * 1000);
        sunSetTextView.setText(sdf.format(calendar.getTime()));
    }

    private void rotateAndSetWindArrow(Data data) {
        Bitmap bmpOriginal = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_arrow);
        Bitmap bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bmResult);
        tempCanvas.rotate(data.getWindDegree(), bmpOriginal.getWidth() / 2, bmpOriginal.getHeight() / 2);
        tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);

        windArrowImageView.setImageBitmap(bmResult);
    }

    @Override
    public void serviceSucces(Drawable mapImage) {
        progressDialog.hide();
        mapImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mapImageView.setImageDrawable(mapImage);

    }

    @Override
    public void serviceFail(Exception exception) {
        progressDialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateIcon(Drawable icon) {
        progressDialog.hide();
        conditionImageView.setImageDrawable(icon);

    }

    public void getWeather_Click(View view) {
        location = cityEditText.getText().toString();
        weatherService.setLocation(location);
        weatherService.getWeatherData();
        progressDialog.show();

        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

}

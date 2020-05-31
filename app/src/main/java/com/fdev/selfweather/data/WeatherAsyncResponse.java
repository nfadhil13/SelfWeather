package com.fdev.selfweather.data;

import android.graphics.Bitmap;

import com.fdev.selfweather.model.Weather;

public interface WeatherAsyncResponse {
    void processFinished(Weather weather);
    void processFinished(Bitmap bitmap);
}

package com.fdev.selfweather.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fdev.selfweather.controller.AppController;
import com.fdev.selfweather.controller.ImageController;
import com.fdev.selfweather.model.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class WeatherBank {



    ArrayList<String> cityList = new ArrayList<>(Arrays.asList(new String[] {"Bandung","Jakarta","Surabaya","Seoul","Soreang",
    "Cirebon" , "Daegu" , "Malang" , "Yogyakarta" , "Incheon"}));


    private final String URL_PREFIX = "https://api.openweathermap.org/data/2.5/weather?";
    private String URL_QUERY = "q=";

    private final String URL_LANGUAGE_QUERY = "&lang=";
    private String languagePref = "en";

    private final String URL_UNITS_QUERY = "&units=";
    private String unitsPref = "metric";
    private String unitsLogo = "C" + "\u00B0";

    private final String URL_SUFFIX = "&APPID=18e41e8f44be80f8e7807e154de788c6";

    private final String IMAGE_URL_PREFIX = "https://openweathermap.org/img/wn/";

    private final String IMAGE_URL_SUFFIX = "@2x.png";


    public Weather getWeather(final int index , final WeatherAsyncResponse callback){


        String url = URL_PREFIX +
                URL_QUERY + cityList.get(index) +  //Get By City Name
                URL_LANGUAGE_QUERY + languagePref +  // Set the Language Pref
                URL_UNITS_QUERY + unitsPref // Unit Pref Celcius, Farenheit ,etc
                +URL_SUFFIX ;
        System.out.println(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    Weather currentWeather = null;
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        String city = cityList.get(index);
                        try {

                            // Get the date
                            long unixTime = response.getLong("dt");
                            Date date = new Date(unixTime*1000);

                            //Get Temperature
                            JSONObject mainObject = response.getJSONObject("main");
                            String temperature = mainObject.getString("temp") + " " + unitsLogo ;


                            //Get Weather Description
                            JSONObject weatherObject = response.getJSONArray("weather").getJSONObject(0);
                            String description = weatherObject.getString("description");


                            //Get Weather Image Bitmap
                            String icon = weatherObject.getString("icon");
                            String iconUrl = IMAGE_URL_PREFIX + icon + IMAGE_URL_SUFFIX;


                            currentWeather = new Weather(city,date,temperature,description,iconUrl);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //weather = null;
                        }

                        if(null != callback) {
                            callback.processFinished(currentWeather);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error" , "Error Happened When Requesting an URL");
                        if(null != callback) callback.processFinished((Weather) null);
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
        return null;
    }

    public Bitmap getBitmap(final String url , final WeatherAsyncResponse callBack , Context context){
        final Bitmap bitmap = null;
            ImageRequest imageRequest = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            if(callBack != null){
                                callBack.processFinished(response);
                            }
                        }
                    }, 0, 0, ScaleType.CENTER, Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
        ImageController.getInstance(context).addToRequestQueue(imageRequest);
        return bitmap;

    }

    public int getListLength(){
        return cityList.size()-1;
    }

    public String getLanguagePref() {
        return languagePref;
    }

    public void setLanguagePref(String languagePref) {
        this.languagePref = languagePref;
    }

    public String getUnitsPref() {
        return unitsPref;
    }

    public void setUnitsPref(String unitsPref) {
        this.unitsPref = unitsPref;
        this.unitsLogo = getLogo(unitsPref) + "\u00B0" ;
    }

    public String getLogo(String units){
        if(units.equals("metric")){
            return "C";
        }
        return "F";
    }

    public void addCity(String city){
        cityList.add(city);
    }


}

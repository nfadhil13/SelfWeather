package com.fdev.selfweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.fdev.selfweather.data.WeatherAsyncResponse;
import com.fdev.selfweather.data.WeatherBank;
import com.fdev.selfweather.model.Weather;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity
        implements WeatherAsyncResponse ,
        Animation.AnimationListener ,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String SAVED_INSTANCE_INDEX= "index";
    private static final String SAVED_INSTANCE_ANIMATION_INDEX = "animation";
    private static final String PREFERENCE_ID = "SAVED";

    private static final int FETCH_LOADER = 19;

    private int mCurrentIndex;
    private int currentAnimation;

    private TextView mCityTextView;
    private TextView mTemperatureTextView;
    private TextView mLastTimeUpdateTextView;
    private TextView mDescriptionTextView;
    private TextView mWeatherCounterTextView;

    private ProgressBar progressBar;

    private Animation[] shakeAnimation;

    private ImageView mWeatherIcon;

    private static final WeatherBank mWeatherBank = new WeatherBank();

    private Weather mCurrentWeather;

    private ImageButton mNextButton;
    private ImageButton mPrefButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mTemperatureTextView = findViewById(R.id.tv_temperature);
        mLastTimeUpdateTextView = findViewById(R.id.tv_lastUpdate);
        mDescriptionTextView = findViewById(R.id.tv_description);
        mWeatherCounterTextView = findViewById(R.id.tv_weatherCounter);


        mCityTextView = findViewById(R.id.tv_city);

        progressBar = findViewById(R.id.progressBar);

        mWeatherIcon = findViewById(R.id.imgView_icon);

        mNextButton = findViewById(R.id.btn_next);
        mPrefButton = findViewById(R.id.btn_before);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f , 0.0f);
        alphaAnimation.setDuration(750);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        shakeAnimation = new Animation[]{
                AnimationUtils.loadAnimation(this , R.anim.shake_animation),
                AnimationUtils.loadAnimation(this,R.anim.shake_left_animation),
                alphaAnimation
        };

        for(int i =0 ; i< shakeAnimation.length ; i++){
            shakeAnimation[i].setAnimationListener(this);
        }



        if(savedInstanceState!=null){
            currentAnimation = savedInstanceState.getInt(SAVED_INSTANCE_ANIMATION_INDEX,0);
            mCurrentIndex  = savedInstanceState.getInt(SAVED_INSTANCE_INDEX,0);
        }else{
            currentAnimation = 0;
            mCurrentIndex =0;
        }



            setSharedPreference();
            mWeatherCounterTextView.setText(mCurrentIndex+1 + "/" + String.valueOf((mWeatherBank.getListLength())+1));
            beforeFetch();

            mWeatherBank.getWeather(mCurrentIndex , this );

    }
    private void setSharedPreference(){

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        mWeatherBank.setUnitsPref(sharedPreferences.getString(getString(R.string.pref_units_key),
                getString(R.string.pref_units_metric)));

        mWeatherBank.setLanguagePref(sharedPreferences.getString(getString(R.string.pref_language_key),
                getString(R.string.pref_language_en)));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void processFinished(Weather weather) {
        if(weather != null){
            Log.d("Weather Condition" , "Weather Tidak Null");
            mCurrentWeather = weather;
            mWeatherBank.getBitmap(weather.getBitMapUrl(),this,this);
        }else{
            Toast.makeText(this,"Error Pls Check Your Connection  \n Or  \n City Input"
                    ,Toast.LENGTH_LONG).show();
            onNext(getCurrentFocus());
        }
    }

    @Override
    public void processFinished(Bitmap bitmap) {
        if(bitmap != null){
            Log.d("bitmap Condition" , "bitmap Tidak Null");
            mCityTextView.setText(mCurrentWeather.getCity());
            mTemperatureTextView.setText(mCurrentWeather.getTemperature());
            String lastTimeUpdateText = getString(R.string.tv_last_update)+ " "
                    + new SimpleDateFormat("HH:mm").format(mCurrentWeather.getDate());
            mLastTimeUpdateTextView.setText(lastTimeUpdateText);
            mDescriptionTextView.setText(mCurrentWeather.getWeatherDescription());
            mWeatherIcon.setImageBitmap(bitmap);
            mWeatherCounterTextView.setText(mCurrentIndex+1 + "/" + String.valueOf((mWeatherBank.getListLength())+1));
            afterFetch();
            shakeAnimation();
        }else{
            Log.d("bitmap Condition" , "bitmap Null");
        }
    }


    public void onNext(View view) {
        mCurrentIndex++;
        beforeFetch();
        if(mCurrentIndex>mWeatherBank.getListLength()){
                mCurrentIndex = 0;
                mWeatherBank.getWeather(mCurrentIndex , this);


        }else{
            mWeatherBank.getWeather(mCurrentIndex , this);
        }

    }

    public void onBefore(View view) {
        mCurrentIndex--;
        beforeFetch();
        if(mCurrentIndex<0){
                mCurrentIndex = mWeatherBank.getListLength();
                mWeatherBank.getWeather(mCurrentIndex,this);


        }else{
            mWeatherBank.getWeather(mCurrentIndex,this);
        }
    }

    private void beforeFetch(){
        mDescriptionTextView.setVisibility(View.INVISIBLE);
        mWeatherIcon.setVisibility(View.INVISIBLE);
        mLastTimeUpdateTextView.setVisibility(View.INVISIBLE);
        mTemperatureTextView.setVisibility(View.INVISIBLE);
        mCityTextView.setVisibility(View.INVISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
        mPrefButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void afterFetch(){
        progressBar.setVisibility(View.INVISIBLE);
        mTemperatureTextView.setVisibility(View.VISIBLE);
        mCityTextView.setVisibility(View.VISIBLE);
        mDescriptionTextView.setVisibility(View.VISIBLE);
        mWeatherIcon.setVisibility(View.VISIBLE);
        mLastTimeUpdateTextView.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
        mPrefButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_INDEX,mCurrentIndex);
        outState.putInt(SAVED_INSTANCE_ANIMATION_INDEX,currentAnimation);


    }

    private void shakeAnimation(){

        CardView cardView = findViewById(R.id.cardView2);
        cardView.setAnimation(shakeAnimation[currentAnimation]);
        currentAnimation = (currentAnimation+1) % shakeAnimation.length;
    }


    @Override
    public void onAnimationStart(Animation animation) {
        mPrefButton.setEnabled(false);
        mNextButton.setEnabled(false);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mPrefButton.setEnabled(true);
        mNextButton.setEnabled(true);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedId = item.getItemId();

        if(selectedId == R.id.go_to_setting){
            Intent startSettingActivity = new Intent(this , SettingActivity.class);
            startActivity(startSettingActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("Key :" , "Ini adalah " + key + " nya");
        if(key.equals(getString(R.string.pref_units_key))){
            String unitPref = sharedPreferences.getString(key, getString(R.string.pref_units_metric));
            mWeatherBank.setUnitsPref(unitPref);
            Log.d("UnitPref :" , unitPref  + "  : (yang asli)"  + mWeatherBank.getUnitsPref());
        }else if(key.equals(getString(R.string.pref_language_key))){
            String languagePref = sharedPreferences.getString(key,getString(R.string.pref_language_en));
            mWeatherBank.setLanguagePref(languagePref);
        }
        mWeatherBank.getWeather(mCurrentIndex,this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("destroyed","ok");
        Context context;
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}

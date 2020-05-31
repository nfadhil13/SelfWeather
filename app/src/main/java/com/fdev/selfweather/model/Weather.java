package com.fdev.selfweather.model;

import java.util.Date;

public class Weather {

    private String city;
    private Date date;
    private String temperature;
    private String weatherDescription;
    private String bitMapUrl;


    public Weather() {
    }

    public Weather(String city, Date date, String temperature, String weatherDescription, String bitMapUrl) {
        this.city = city;
        this.date = date;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
        this.bitMapUrl = bitMapUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getBitMapUrl() {
        return bitMapUrl;
    }

    public void setBitMapUrl(String bitMapUrl) {
        this.bitMapUrl = bitMapUrl;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city='" + city + '\'' +
                ", date=" + date +
                ", temperature='" + temperature + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                '}';
    }
}

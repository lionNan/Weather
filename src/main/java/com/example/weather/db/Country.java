package com.example.weather.db;

/**
 * Created by lion on 2016/10/11.
 */
public class Country {
    private int id;
    private String countryName;
    private String countryCode;
    private int cityId;

    public void setId(int id) {
        this.id = id;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getId() {

        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public int getCityId() {
        return cityId;
    }
}

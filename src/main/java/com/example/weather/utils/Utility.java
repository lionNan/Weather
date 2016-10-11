package com.example.weather.utils;

import android.text.TextUtils;

import com.example.weather.db.City;
import com.example.weather.db.Country;
import com.example.weather.db.Province;
import com.example.weather.db.WeatherDB;

import org.w3c.dom.Text;

/**
 * Created by lion on 2016/10/11.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */

    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces!=null && allProvinces.length>0){
                for (String c:allProvinces){
                    String [] array = c.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[0]);
                    province.setProvinceName(array[1]);

                    //将解析的数据存储到City表
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities!=null && allCities.length>0){
                for (String c:allCities){
                    String [] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析的数据存储到City表
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountriesResponse (WeatherDB db,String response,int cityId){

        if (!TextUtils.isEmpty(response)){
            String [] allcountries = response.split(",");
            if (allcountries!=null && allcountries.length>0){
                for (String c:allcountries){
                    String[] array = c.split("\\|");
                    Country country = new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    //将解析出来的数据存储到Country表
                    db.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }

}

package com.example.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lion on 2016/10/11.
 */
public class WeatherDB  {
    /*
    数据库名
     */
    public static final String DB_NAME="weather";
    /*
    数据库版本
     */
    public static final int VERSION =1;
    private static WeatherDB weatherDB;
    private SQLiteDatabase db;
    /*
    构造方法私有化
     */

    public WeatherDB(Context context) {
        WeatherOpenHelper dbHlper = new WeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHlper.getWritableDatabase();
    }
    /*
    获取WeatherDB的实例
     */
    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }
    /*
    将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }
    /*
    从数据库读取全国所有省份的信息
     */
    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<>();
        Cursor cursor =db
                .query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /*
    将City实例存储到数据库
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }

    }
    /*
      从数据库下读取某省下所有的城市信息
       */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();
        Cursor cursor =db
                .query("City",null,"province_id =?",new String[]{String.valueOf(provinceId)},null,null,null);
        if (cursor.moveToFirst()){
            do {
               City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
            if (cursor!=null){
                cursor.close();
            }
        }
        return list;
    }
    /*
    将Country实例存储到数据库
     */
    public void saveCountry(Country country){
        if (country != null){
            ContentValues values = new ContentValues();
            values.put("country_name",country.getCountryName());
            values.put("country_code",country.getCountryCode());
            values.put("city_id",country.getCityId());
            db.insert("Country",null,values);
        }
    }
    /*
    从数据库读取某城市下所有的县信息
     */
    public List<Country> loadCountries(int cityId){
        List<Country> list = new ArrayList<>();
        Cursor cursor = db.query("Country",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if (cursor.moveToFirst()){
            Country country = new Country();
            country.setId(cursor.getInt(cursor.getColumnIndex("id")));
            country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
            country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
            country.setCityId(cityId);
            list.add(country);
        }while (cursor.moveToNext());
    return list;
    }

}

package com.example.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lion on 2016/10/11.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {
    /**
     *Province表建表语句
     */
    public static  final  String CREATE_PROVINCE ="create table Province ("
            +"id integer primary key autoincrement,"
            +"province_name text,"
            +"province_code text)";
    /*
    City表建表语句
     */
    public static final String CEATE_CITY = "Create table City ("
            +"id integer key primary autoincrement,"
            +"city_name text,"
            +"city_code text,"
            +"privince_id integer)";
    /*
    country表建表语句
     */
    public static final String CREATE_COUNTRY ="create table Country("
            +"id integer key primary autoincrement,"
            +"country_name text,"
            +"country_code text,"
            +"city_id integer)";
    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_PROVINCE);
    db.execSQL(CEATE_CITY);
        db.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

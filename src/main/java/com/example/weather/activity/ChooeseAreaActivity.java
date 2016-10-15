package com.example.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.R;
import com.example.weather.db.City;
import com.example.weather.db.Country;
import com.example.weather.db.Province;
import com.example.weather.db.WeatherDB;
import com.example.weather.utils.HttpCallbackListener;
import com.example.weather.utils.HttpUtil;
import com.example.weather.utils.Utility;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lion on 2016/10/11.
 */
public class ChooeseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;
    private TextView titletext;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<Country> countryList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 选中的县
     */
    private Country selectedCountry;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    /**
     * 是否从WeatherActivity中跳转过来
     * @param savedInstanceState
     */
    private boolean isFromWeatherActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity

        if (prefs.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titletext = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCities();
                }else if (currentLevel==LEVEL_COUNTRY){
                    String countryCode = countryList.get(index).getCountryCode();
                    Intent intent = new Intent(ChooeseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("country_code",countryCode);
                    startActivity(intent);
                    finish();
                }
            }
            });
            queryProvinces();//加载省级数据
        }


    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        provinceList = weatherDB.loadProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        cityList = weatherDB.loadCities(selectedCity.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;

        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }


    }
    /**
     * 查询市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCountries(){
        countryList=weatherDB.loadCountries(selectedCity.getId());
        if (countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTRY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"country");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询市县数据
     */
        private void queryFromServer(final String code,final String type){
            String address;
            if (!TextUtils.isEmpty(code)){
                address="https://www.weather.com.cn/data/list3/city"+code+".xml";
            }else {
                address = "http://www.weather.com.cm/data/list3/city.xml";
            }
            showProgressDialog();
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    boolean result =false;
                    if ("province".equals(type)){
                        result = Utility.handleProvincesResponse(weatherDB,response);
                    }else if ("city".equals(type)){
                        result=Utility.handleCitiesResponse(weatherDB,response,selectedProvince.getId());
                    }else if ("country".equals(type)){
                        result = Utility.handleCountriesResponse(weatherDB,response,selectedCity.getId());
                    }
                    if (result){
                        //通过runOnUiThread方法回到主线程处理逻辑
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if ("province".equals(type)){
                                    queryProvinces();
                                }else if ("city".equals(type)){
                                    queryCities();
                                }else if ("country".equals(type)){
                                    queryCountries();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    //通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooeseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });

        }
    /**
     * 显示进度对话框
     */
      private void showProgressDialog(){
          if (progressDialog!=null){
              progressDialog = new ProgressDialog(this);
              progressDialog.setMessage("正在加载。。。。");
              progressDialog.setCanceledOnTouchOutside(false);
          }
          progressDialog.show();
      }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTRY){
            queryCities();
        }else if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            if (isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
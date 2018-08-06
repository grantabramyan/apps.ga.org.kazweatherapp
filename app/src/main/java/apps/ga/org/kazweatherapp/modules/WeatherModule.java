package apps.ga.org.kazweatherapp.modules;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import apps.ga.org.kazweatherapp.domain.City;
import dagger.Module;
import dagger.Provides;

/**
 * Created by grant on 03,August,2018
 */
@Module(includes = {AppModule.class})
public class WeatherModule {
    private SharedPreferences sharedPreferences;
    @Nullable private String searchText;
    private List<City> cities;
    private Date modifiedDate;


    @Inject
    public WeatherModule(@NonNull SharedPreferences sharedPreferences) {
        this.sharedPreferences=sharedPreferences;
        this.searchText = sharedPreferences.getString("searchText",null);
        String json=sharedPreferences.getString("cities",null);
        this.cities=TextUtils.isEmpty(json) ? new ArrayList<>() : new Gson().fromJson(json,new TypeToken<List<City>>(){}.getType());
        long time=sharedPreferences.getLong("modifiedDate",-1);
        if(time>0){
            this.modifiedDate=new Date(time);
        }
    }

    @Provides
    @Singleton
    @NonNull
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Provides
    @Singleton
    @Nullable
    public String getSearchText(){
        return searchText;
    }

    @Provides
    @Singleton
    @NonNull
    public List<City> getCities() {
        return cities;
    }

    @Provides
    @Singleton
    @Nullable
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setSearchText(String searchText){
        this.searchText=searchText;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void savSearchText(String searchText){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("searchText",searchText);
        editor.apply();
    }

    public void saveCities(List<City> cities){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cities",new Gson().toJson(cities,new TypeToken<List<City>>(){}.getType()));
        editor.apply();
    }
    public void saveModifiedDate(Date modifiedDate){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("modifiedDate",modifiedDate==null ? -1 : modifiedDate.getTime());
        editor.apply();
    }

    public void saveCache(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("searchText",searchText);
        editor.putString("cities",new Gson().toJson(cities,new TypeToken<List<City>>(){}.getType()));
        editor.putLong("modifiedDate",modifiedDate==null ? -1 : modifiedDate.getTime());
        editor.apply();
    }
}

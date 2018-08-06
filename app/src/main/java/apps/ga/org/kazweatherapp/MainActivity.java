package apps.ga.org.kazweatherapp;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import apps.ga.org.kazweatherapp.activities.BaseAppCompatActivity;
import apps.ga.org.kazweatherapp.adapters.CityAdapter;
import apps.ga.org.kazweatherapp.api.WeatherRestBuilder;
import apps.ga.org.kazweatherapp.domain.City;
import apps.ga.org.kazweatherapp.domain.Weather;
import apps.ga.org.kazweatherapp.modules.AppModule;
import apps.ga.org.kazweatherapp.modules.WeatherModule;
import apps.ga.org.kazweatherapp.utils.Utils;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseAppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppCompatEditText searchTextEditText;
    private RecyclerView cityRecyclerView;
    private CityAdapter adapter;
    private long lastTextChangedTime;

    @Inject
    WeatherModule weatherModule;

    @Inject
    AppModule appModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.getWeatherComponent().inject(this);
        initUI();
    }

    private void initUI() {
        cityRecyclerView = findViewById(R.id.cityRecyclerView);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new CityAdapter(this, weatherModule.getCities(), (city, position) -> {

        });
        cityRecyclerView.setAdapter(adapter);

        searchTextEditText=findViewById(R.id.search_text);
        searchTextEditText.setText(weatherModule.getSearchText());

        if(weatherModule.getModifiedDate()!=null &&
                System.currentTimeMillis()-weatherModule.getModifiedDate().getTime()>TimeUnit.HOURS.toMillis(1)){
            getWeather(weatherModule.getCities());
            weatherModule.setModifiedDate(new Date());
            weatherModule.saveModifiedDate(new Date());
        }
        searchTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lastTextChangedTime = System.currentTimeMillis();
                if (s.length() > 1) {
                    searchTextEditText.postDelayed(() -> {
                        if (System.currentTimeMillis() - lastTextChangedTime >= 300) {
                            weatherModule.setSearchText(s.toString());
                            weatherModule.savSearchText(weatherModule.getSearchText());
                            if(!Utils.isInternetAvailable(getBaseContext())){
                                Toast.makeText(getBaseContext(),R.string.noInternet,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            getCities(weatherModule.getSearchText()).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSubscribe(subscription -> {

                                    })
                                    .subscribe(cities -> {
                                        weatherModule.setCities(cities);
                                        weatherModule.saveCities(cities);

                                        weatherModule.setModifiedDate(new Date());
                                        weatherModule.saveModifiedDate(weatherModule.getModifiedDate());

                                        appModule.getApp().hideKeyBoard();
                                        searchTextEditText.clearFocus();
                                        CityDiffUtilCallBack cityDiffUtilCallBack=new CityDiffUtilCallBack(adapter.getData(),cities);
                                        DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(cityDiffUtilCallBack,false);
                                        adapter.setData(cities);
                                        diffResult.dispatchUpdatesTo(adapter);
                                        getWeather(cities);
                                    }, throwable -> {

                                    }, () -> {

                                    });
                        }
                    }, 300);
                }
            }
        });
    }

    private Flowable<List<City>> getCities(String searchText) {
        return Flowable.fromCallable(() -> {
            if (appModule.getGoogleApiClient() != null && appModule.getGoogleApiClient().isConnected()) {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                        .setCountry("KZ")
                        .build();
                PendingResult<AutocompletePredictionBuffer> pendingResult = Places.GeoDataApi.getAutocompletePredictions(appModule.getGoogleApiClient(), searchText, null, typeFilter);
                // Wait for predictions, set the timeout.
                AutocompletePredictionBuffer autocompletePredictions = pendingResult.await(60, TimeUnit.SECONDS);
                Status status=autocompletePredictions.getStatus();
                if(!status.isSuccess()){
                    Log.d(TAG,"status: "+status.getStatusMessage());
                    autocompletePredictions.release();
                    runOnUiThread(()->{Toast.makeText(getApplicationContext(),getString(R.string.can_not_get_cities),Toast.LENGTH_SHORT).show();});
                    return null;
                }
                List<City> cities=new ArrayList<>(autocompletePredictions.getCount());
                Iterator<AutocompletePrediction> iterator=autocompletePredictions.iterator();
                while (iterator.hasNext()){
                    AutocompletePrediction autocompletePrediction=iterator.next();
                    Log.d(TAG,String.format("placeId:%s title:%s",autocompletePrediction.getPlaceId(),autocompletePrediction.getFullText(null).toString()));
                    City city=new City();
                    city.setName(autocompletePrediction.getPrimaryText(null).toString());
                    city.setGooglePlaceId(autocompletePrediction.getPlaceId());
                    cities.add(city);
                }
                autocompletePredictions.release();
                return cities;
            }
            return Collections.emptyList();
        });
    }

    /*private Flowable<List<City>> getCities(String searchText) {
        Log.d(TAG,"main>>>>>>>>>>>>>>>>>>>>:"+(Looper.getMainLooper()==Looper.myLooper()));
        return Flowable.fromCallable(() -> {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                            .setCountry("KZ")
                        .build();
                Task<AutocompletePredictionBufferResponse> predictionBufferResponseTask = geoDataClient.getAutocompletePredictions(searchText,null,typeFilter);
                predictionBufferResponseTask.addOnCompleteListener(task -> {
                    AutocompletePredictionBufferResponse response=task.getResult();
                    Log.d(TAG,"response count>>>>>>>>>>>>>>>: "+response.getCount());
                    List<City> cities=new ArrayList<>(response.getCount());
                    for (AutocompletePrediction autocompletePrediction:response){
                        Log.d(TAG,String.format("placeId:%s title:%s",autocompletePrediction.getPlaceId(),autocompletePrediction.getFullText(null).toString()));
                        City city=new City();
                        city.setName(autocompletePrediction.getPrimaryText(null).toString());
                        city.setGooglePlaceId(autocompletePrediction.getPlaceId());
                        cities.add(city);
                    }
                    response.release();
                });
            }
            return Collections.emptyList();
        });
    }*/

    private void getWeather(List<City> cities){
        for(City city:cities){//TODO prevent loop
                Log.d(TAG,"name: "+city.getName());
                WeatherRestBuilder
                        .getInstance()
                        .getApi()
                        .getWeather(city.getName(),"metric","5b4425f8b081f037b5e9d68c1803a7c9")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Weather>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Weather weather) {
                                city.setWeather(weather);
//                                List<City> oldCities=adapter.getData();
//                                city.setWeather(weather);
//                                List<City> newCities=adapter.getData();
//                                WeatherDiffUtilCallBack weatherDiffUtilCallBack=new WeatherDiffUtilCallBack(oldCities,newCities);
//                                DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(weatherDiffUtilCallBack,true);
//                                diffResult.dispatchUpdatesTo(adapter);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                adapter.notifyDataSetChanged();
                                weatherModule.setCities(cities);
                                weatherModule.saveCities(cities);
                            }
                        });
        }

    }

    class CityDiffUtilCallBack extends DiffUtil.Callback{
        private List<City> oldList;
        private List<City> newList;

        public CityDiffUtilCallBack(List<City> oldList, List<City> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getGooglePlaceId().equals(newList.get(newItemPosition).getGooglePlaceId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            City oldCity=oldList.get(oldItemPosition);
            City newCity=newList.get(newItemPosition);
            return oldCity.getGooglePlaceId().equals(newCity.getGooglePlaceId()) & oldCity.getName().equals(newCity.getName());
        }
    }

    class WeatherDiffUtilCallBack extends DiffUtil.Callback{
        private List<City> oldList;
        private List<City> newList;

        public WeatherDiffUtilCallBack(List<City> oldList, List<City> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getGooglePlaceId().equals(newList.get(newItemPosition).getGooglePlaceId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            City oldCity=oldList.get(oldItemPosition);
            City newCity=newList.get(newItemPosition);
            if((oldCity.getWeather()==null & newCity.getWeather()!=null)
                    || (oldCity.getWeather()!=null & newCity.getWeather()==null) ){
                return false;
            }
             if (oldCity.getWeather()!=null & newCity.getWeather()!=null){
                 if((oldCity.getWeather().getMainData()==null & newCity.getWeather().getMainData()!=null)
                         || (oldCity.getWeather().getMainData()!=null & newCity.getWeather().getMainData()==null) ){
                     return false;
                 }
                 return Float.compare(oldCity.getWeather().getMainData().getTemperature(),newCity.getWeather().getMainData().getTemperature())==0;
             }
             return false;
        }
    }
}

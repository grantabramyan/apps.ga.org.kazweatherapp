package apps.ga.org.kazweatherapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import apps.ga.org.kazweatherapp.components.DaggerWeatherComponent;
import apps.ga.org.kazweatherapp.components.WeatherComponent;
import apps.ga.org.kazweatherapp.modules.AppModule;
import apps.ga.org.kazweatherapp.modules.WeatherModule;

/**
 * Created by grant on 03,August,2018
 */
public class App extends Application{
    private static final String TAG=App.class.getSimpleName();
    private Activity currentActivity;
    private static WeatherComponent weatherComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if(weatherComponent==null){
                    currentActivity=activity;
                    weatherComponent=buildWeatherComponent();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity=activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }



    public void hideKeyBoard(){
        InputMethodManager inputManager = (InputMethodManager) currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(currentActivity.getWindow().getDecorView().findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    WeatherComponent buildWeatherComponent(){
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage((FragmentActivity) currentActivity, 0, connectionResult -> Log.i(TAG, "Google Places API onConnectionFailed >>>>>>>>>>>>>>>>>>>>>>>>>> : " + connectionResult.getErrorMessage()))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i(TAG, "Google Places API onConnected>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i(TAG, "Google Places API onConnectionSuspended>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    }
                })
                .build();

        SharedPreferences sharedPreferences=getSharedPreferences("cache", Context.MODE_PRIVATE);
        return DaggerWeatherComponent.builder()
                .appModule(new AppModule(getApplicationContext(),this,googleApiClient))
                .weatherModule(new WeatherModule(sharedPreferences))
                .build();
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public static WeatherComponent getWeatherComponent() {
        return weatherComponent;
    }
}

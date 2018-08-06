package apps.ga.org.kazweatherapp.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import apps.ga.org.kazweatherapp.App;
import dagger.Module;
import dagger.Provides;

/**
 * Created by grant on 06,August,2018
 */
@Module
public class AppModule {
    private Context appContext;
    private App app;
    private GoogleApiClient googleApiClient;

    @Inject
    public AppModule(@NonNull Context appContext, @NonNull App app, @NonNull GoogleApiClient googleApiClient) {
        this.appContext = appContext;
        this.app = app;
        this.googleApiClient = googleApiClient;
    }

    @Provides
    @Singleton
    @NonNull
    public Context getAppContext(){
        return appContext;
    }

    @Provides
    @Singleton
    @NonNull
    public App getApp(){
        return app;
    }

    @Provides
    @Singleton
    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }


}

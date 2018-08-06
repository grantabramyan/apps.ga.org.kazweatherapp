package apps.ga.org.kazweatherapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by grant on 03,August,2018
 */
public class WeatherRestBuilder {
    public static WeatherRestBuilder instance;
    private WeatherApi api;
    private final String BASE_URL="http://api.openweathermap.org/data/2.5/";

    private WeatherRestBuilder(){
        init();
    }

    public static WeatherRestBuilder getInstance(){
        if(instance==null){
            instance=new WeatherRestBuilder();
        }
        return instance;
    }

    private void init(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(5, TimeUnit.MINUTES);
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        httpClient.writeTimeout(5,TimeUnit.MINUTES);
        httpClient.addInterceptor(logging);

        Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();
        api=retrofit.create(WeatherApi.class);
    }

    public WeatherApi getApi() {
        return api;
    }
}

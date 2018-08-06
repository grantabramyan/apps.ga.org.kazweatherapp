package apps.ga.org.kazweatherapp.api;

import apps.ga.org.kazweatherapp.domain.Weather;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by grant on 03,August,2018
 */
public interface WeatherApi {
    @GET("weather")
    Observable<Weather> getWeather(@Query("q") String cityName, @Query("units") String units, @Query("APPID") String openWeatherApiKey);

    @GET("weather")
    Call<Weather> getWeather2(@Query("q") String cityName, @Query("units") String units, @Query("APPID") String openWeatherApiKey);
}

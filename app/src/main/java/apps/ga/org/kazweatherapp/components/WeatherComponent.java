package apps.ga.org.kazweatherapp.components;

import javax.inject.Singleton;

import apps.ga.org.kazweatherapp.MainActivity;
import apps.ga.org.kazweatherapp.modules.AppModule;
import apps.ga.org.kazweatherapp.modules.WeatherModule;
import dagger.Component;

/**
 * Created by grant on 06,August,2018
 */
@Component(modules = {AppModule.class,WeatherModule.class})
@Singleton
public interface WeatherComponent {
    void inject(MainActivity mainActivity);
}

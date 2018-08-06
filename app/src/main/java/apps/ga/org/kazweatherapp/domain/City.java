package apps.ga.org.kazweatherapp.domain;


/**
 * Created by grant on 03,August,2018
 */
public class City {
    private int id;
    private String name;
    private String googlePlaceId;
    private Weather weather;


    public City() {

    }

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public City(int id, String name, Weather weather) {
        this.id = id;
        this.name = name;
        this.weather = weather;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

}

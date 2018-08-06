package apps.ga.org.kazweatherapp.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by grant on 03,August,2018
 */
public class Weather {
    @SerializedName("main")
    @Expose
    private MainData mainData;

    public MainData getMainData() {
        return mainData;
    }

    public void setMainData(MainData mainData) {
        this.mainData = mainData;
    }

    public class MainData {
        @SerializedName("temp")
        @Expose
        private float temperature;

        @SerializedName("pressure")
        @Expose
        private float pressure;

        @SerializedName("humidity")
        @Expose
        private int humidity;

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }

        public float getPressure() {
            return pressure;
        }

        public void setPressure(float pressure) {
            this.pressure = pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }


}

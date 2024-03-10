package service.weather;

import service.weather.enums.TimeOfDay;
import service.weather.enums.WeatherCondition;

import java.sql.Timestamp;

public interface CurrentWeather {
    Long getLocationId();
    void setLocationId(Long locationId);
    String getName();
    Timestamp getDate();
    TimeOfDay getTimeOfDay();
    double getTemperature();
    double getFeelsLikeTemp();
    String getState();
    WeatherCondition getCondition();
    double getTempMin();
    double getTempMax();
    int getClouds();
    int getHumidity();
    int getWindDeg();
    double getWindSpeed();
    double getPressure();
    Timestamp getSunrise();
    Timestamp getSunset();
    Double getLatitude();
    Double getLongitude();
}

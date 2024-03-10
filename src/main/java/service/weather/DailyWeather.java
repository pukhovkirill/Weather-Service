package service.weather;

import service.weather.enums.TimeOfDay;
import service.weather.enums.WeatherCondition;

import java.sql.Timestamp;
import java.util.List;

public interface DailyWeather {
    String getName();
    List<Forecast> getHourlyForecast();
    List<Forecast> getDailyForecast();
    
    interface Forecast{
        Timestamp getDate();
        double getTemperature();
        double getMaxTemp();
        double getMinTemp();
        TimeOfDay getTimeOfDay();
        WeatherCondition getCondition();
        String getState();
        double getWindSpeed();
        int getWindDeg();
        double getPrecipitation();
        double getPressure();
        int getClouds();
    }
}

package service.weather.factory;

import entity.Location;
import service.weather.WeatherForecast;

public interface WeatherFactory {
    WeatherForecast getWeatherForecast(Location location);
    WeatherForecast getWeatherForecast(double latitude, double longitude);
}

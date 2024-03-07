package service;

import entity.Location;
import service.weather.*;
import service.weather.factory.WeatherFactory;

public class WeatherApiClientService {
    private final WeatherFactory factory;

    public WeatherApiClientService(WeatherFactory factory){
        this.factory = factory;
    }

    public CurrentWeather getCurrentWeather(Location location){
        return this.seeWeatherForecast(location).getCurrent();
    }

    public DailyWeather getDailyWeather(Location location){
        return this.seeWeatherForecast(location).getDaily();
    }

    private WeatherForecast seeWeatherForecast(Location location){
        return this.factory.getWeatherForecast(location);
    }
}

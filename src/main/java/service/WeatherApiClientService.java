package service;

import entity.Location;
import service.weather.*;
import service.weather.factory.WeatherFactory;

public class WeatherApiClientService {
    private final WeatherForecast forecast;

    public WeatherApiClientService(WeatherFactory factory, Location location){
        this.forecast = factory.getWeatherForecast(location);
    }

    public CurrentWeather seeCurrentWeather(){
        return this.forecast.getCurrent();
    }

    public DailyWeather seeDailyWeather(){
        return this.forecast.getDaily();
    }
}

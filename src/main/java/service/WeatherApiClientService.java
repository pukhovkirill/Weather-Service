package service;

import entity.Location;
import service.weather.*;
import service.weather.factory.WeatherFactory;

public class WeatherApiClientService {
    private final WeatherForecast forecast;

    public WeatherApiClientService(WeatherFactory factory, Location location){
        this.forecast = factory.getWeatherForecast(location);
    }

    public WeatherApiClientService(WeatherFactory factory, String locationName){
        this.forecast = factory.getWeatherForecast(locationName);
    }

    public WeatherApiClientService(WeatherFactory factory, double latitude, double longitude){
        this.forecast = factory.getWeatherForecast(latitude, longitude);
    }

    public WeatherForecast seeWeatherForecast(){
        return this.forecast;
    }

    public CurrentWeather seeCurrentWeather(){
        return this.forecast.getCurrent();
    }

    public DailyWeather seeDailyWeather(){
        return this.forecast.getDaily();
    }
}

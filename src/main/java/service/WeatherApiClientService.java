package service;

import entity.Location;
import service.weather.*;
import service.weather.factory.WeatherFactory;

public class WeatherApiClientService {
    private final WeatherFactory factory;

    private WeatherForecast forecast;

    public WeatherApiClientService(WeatherFactory factory){
        this.factory = factory;
    }

    public CurrentWeather getCurrentWeather(Location location){
        if(forecast == null)
            seeWeatherForecast(location);

        return this.forecast.getCurrent();
    }

    public DailyWeather getDailyWeather(Location location){
        if(forecast == null)
            seeWeatherForecast(location);

        return this.forecast.getDaily();
    }

    private void seeWeatherForecast(Location location){
        this.forecast =  this.factory.getWeatherForecast(location);
    }
}

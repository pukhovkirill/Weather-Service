package service.weather.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Location;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import service.GeoLocationService;
import service.weather.CurrentWeather;
import service.weather.DailyWeather;
import service.weather.WeatherForecast;
import utility.PropertiesUtility;

public class OpenWeatherFactory implements WeatherFactory{
    private final String apiKey;
    private final String uri;

    public OpenWeatherFactory(){
        this.apiKey = PropertiesUtility.getApplicationProperty("weather.ow_api");
        this.uri = PropertiesUtility.getApplicationProperty("weather.ow_weather_uri");
    }

    @Override
    public WeatherForecast getWeatherForecast(Location location) {
        return getWeatherForecast(location.getLatitude(), location.getLongitude());
    }

    @Override
    public WeatherForecast getWeatherForecast(String locationName) {
        var geoService = new GeoLocationService();
        var coordinates = geoService.findFirstCoordinateByName(locationName);

        return getWeatherForecast(coordinates.getLatitude(), coordinates.getLongitude());
    }

    @Override
    public WeatherForecast getWeatherForecast(double latitude, double longitude) {
        WeatherForecast forecast = new WeatherForecast();
        try {
            var currentWeather = seeCurrentWeatherForecast(latitude, longitude);
            var dailyWeather = seeDailyWeatherForecast(latitude, longitude);

            forecast.setCurrent(currentWeather);
            forecast.setDaily(dailyWeather);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return forecast;
    }

    private CurrentWeather seeCurrentWeatherForecast(double latitude, double longitude) throws JsonProcessingException {
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get(uri)
                        .header("accept", "application/json")
                        .routeParam("param", "weather")
                        .queryString("lat", latitude)
                        .queryString("lon", longitude)
                        .queryString("appid", apiKey)
                        .asJson();

        var objectMapper = new ObjectMapper();
        var json = jsonResponse.getBody().toString();

        return objectMapper.readValue(json, CurrentWeather.class);
    }

    private DailyWeather seeDailyWeatherForecast(double latitude, double longitude) throws JsonProcessingException {
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get(uri)
                        .header("accept", "application/json")
                        .routeParam("param", "forecast")
                        .queryString("lat", latitude)
                        .queryString("lon", longitude)
                        .queryString("appid", apiKey)
                        .asJson();

        var objectMapper = new ObjectMapper();
        var json = jsonResponse.getBody().toString();

        return objectMapper.readValue(json, DailyWeather.class);
    }
}

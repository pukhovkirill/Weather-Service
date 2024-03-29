package service.weather.factory.OpenWeather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Location;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import service.weather.OWCurrentWeatherAdapter;
import service.weather.OWDailyWeatherAdapter;
import service.weather.WeatherForecast;
import service.weather.factory.WeatherFactory;
import utility.PropertiesUtility;

public class OpenWeatherFactory implements WeatherFactory {
    private final String apiKey;
    private final String uri;
    private final String units = "metric";

    public OpenWeatherFactory(){
        this.apiKey = PropertiesUtility.getApplicationProperty("weather.ow_api");
        this.uri = PropertiesUtility.getApplicationProperty("weather.ow_weather_uri");
    }

    @Override
    public WeatherForecast getWeatherForecast(Location location) {
        return getWeatherForecast(location.getLatitude(), location.getLongitude());
    }

    @Override
    public WeatherForecast getWeatherForecast(double latitude, double longitude) {
        WeatherForecast forecast = new WeatherForecast();
        try {
            var currentWeather = seeCurrentWeatherForecast(latitude, longitude);
            var currentWeatherAdapter = new OWCurrentWeatherAdapter(currentWeather);

            var dailyWeather = seeDailyWeatherForecast(latitude, longitude);
            var dailyWeatherAdapter = new OWDailyWeatherAdapter(dailyWeather);

            forecast.setCurrent(currentWeatherAdapter);
            forecast.setDaily(dailyWeatherAdapter);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return forecast;
    }

    private OWCurrentWeather seeCurrentWeatherForecast(double latitude, double longitude) throws JsonProcessingException {
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get(uri)
                        .header("accept", "application/json")
                        .routeParam("param", "weather")
                        .queryString("lat", latitude)
                        .queryString("lon", longitude)
                        .queryString("appid", apiKey)
                        .queryString("units", units)
                        .asJson();

        var objectMapper = new ObjectMapper();
        var json = jsonResponse.getBody().toString();

        return objectMapper.readValue(json, OWCurrentWeather.class);
    }

    private OWDailyWeather seeDailyWeatherForecast(double latitude, double longitude) throws JsonProcessingException {
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get(uri)
                        .header("accept", "application/json")
                        .routeParam("param", "forecast")
                        .queryString("lat", latitude)
                        .queryString("lon", longitude)
                        .queryString("appid", apiKey)
                        .queryString("units", units)
                        .asJson();

        var objectMapper = new ObjectMapper();
        var json = jsonResponse.getBody().toString();

        return objectMapper.readValue(json, OWDailyWeather.class);
    }
}

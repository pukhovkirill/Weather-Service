package service.weather.factory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Location;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import service.weather.CurrentWeather;
import service.weather.DailyWeather;
import service.weather.WeatherForecast;
import utility.Utilities;

public class OpenWeatherFactory implements WeatherFactory{
    private String apiKey;
    private String uri;

    public OpenWeatherFactory(){
        this.apiKey = Utilities.getOpenWeatherApiKey();
        this.uri = Utilities.getOpenWeatherUri();
    }

    @Override
    public WeatherForecast getWeatherForecast(Location location) {
        return getWeatherForecast(location.getLatitude(), location.getLongitude());
    }

    @Override
    public WeatherForecast getWeatherForecast(String locationName) {
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get("http://api.openweathermap.org/geo/1.0/direct")
                        .header("accept", "application/json")
                        .queryString("q", locationName)
                        .queryString("limit", 1)
                        .queryString("appid", apiKey).asJson();

        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var json = jsonResponse.getBody().toString();

        Coordinate[] coord;
        try {
            coord = objectMapper.readValue(json, Coordinate[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return getWeatherForecast(coord[0].getLat(), coord[0].getLon());
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

    @Getter
    @Setter
    public static class Coordinate{
        @JsonProperty("name")
        private String name;
        @JsonProperty("lat")
        private double lat;
        @JsonProperty("lon")
        private double lon;
    }
}

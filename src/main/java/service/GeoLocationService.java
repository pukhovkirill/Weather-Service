package service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import model.Coordinates;
import utility.Utilities;

public class GeoLocationService {
    private final static String GEO_SERVICE_URI = "http://api.openweathermap.org/geo/1.0/direct";
    private String apiKey;

    public GeoLocationService(){
        this.apiKey = Utilities.getOpenWeatherApiKey();
    }

    public Coordinates findCoordinatesByName(String name){
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get(GEO_SERVICE_URI)
                        .header("accept", "application/json")
                        .queryString("q", name)
                        .queryString("limit", 1)
                        .queryString("appid", apiKey).asJson();

        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var json = jsonResponse.getBody().toString();

        Coordinates[] coordinates;
        try {
            coordinates = objectMapper.readValue(json, Coordinates[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return coordinates[0];
    }
}

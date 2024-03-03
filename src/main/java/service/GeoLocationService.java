package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import model.Coordinates;
import utility.PropertiesUtility;

public class GeoLocationService {
    private final String apiKey;
    private final String uri;

    public GeoLocationService(){
        this.apiKey = PropertiesUtility.getApplicationProperty("weather.ow_api");
        this.uri = PropertiesUtility.getApplicationProperty("weather.ow_geo_location_uri");
    }

    public Coordinates[] findCoordinatesByName(String name){
        HttpResponse<JsonNode> jsonResponse =
                Unirest.get(uri)
                        .header("accept", "application/json")
                        .queryString("q", name)
                        .queryString("limit", 5)
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

        return coordinates;
    }

    public Coordinates findFirstCoordinateByName(String name){
        return findCoordinatesByName(name)[0];
    }
}

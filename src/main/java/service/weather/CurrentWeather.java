package service.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather{
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private Rain rain;
    private Snow snow;
    private long dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;

    @Getter
    @Setter
    public static class Coord {
        private double lon;
        private double lat;
    }

    @Getter
    @Setter
    public static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Getter
    @Setter
    public static class Main {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        private double pressure;
        private int humidity;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        @JsonProperty("sea_level")
        private double seaLevel;
        @JsonProperty("grnd_level")
        private double groundLevel;
    }

    @Getter
    @Setter
    public static class Wind {
        private double speed;
        private int deg;
        private double gust;
    }

    @Getter
    @Setter
    public static class Clouds {
        private int all;
    }

    @Getter
    @Setter
    public static class Rain {
        @JsonProperty("1h")
        private double rain1h;
        @JsonProperty("3h")
        private double rain3h;
    }

    @Getter
    @Setter
    public static class Snow {
        @JsonProperty("1h")
        private double snow1h;
        @JsonProperty("3h")
        private double snow3h;
    }

    @Getter
    @Setter
    public static class Sys {
        private int type;
        private int id;
        private double message;
        private String country;
        private long sunrise;
        private long sunset;
    }
}

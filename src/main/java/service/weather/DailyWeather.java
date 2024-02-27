package service.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyWeather{
    private String cod;
    private String message;
    private int cnt;
    private List<Forecast> list;
    private City city;

    @Getter
    @Setter
    public static class Forecast {
        private long dt;
        private Main main;
        private List<Weather> weather;
        private Clouds clouds;
        private Wind wind;
        private int visibility;
        private int pop;
        private Rain rain;
        private Snow snow;
        private Sys sys;
        @JsonProperty("dt_txt")
        private String dtTxt;
    }

    @Getter
    @Setter
    public static class Main {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        private double pressure;
        @JsonProperty("sea_level")
        private double seaLevel;
        @JsonProperty("grnd_level")
        private double groundLevel;
        private int humidity;
        @JsonProperty("temp_kf")
        private double tempKf;
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
    public static class Clouds {
        private int all;
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
    public static class Rain {
        @JsonProperty("3h")
        private double rain3h;
    }

    @Getter
    @Setter
    public static class Snow {
        @JsonProperty("3h")
        private double snow3h;
    }

    @Getter
    @Setter
    public static class Sys {
        private String pod;
    }

    @Getter
    @Setter
    public static class City {
        private int id;
        private String name;
        private Coord coord;
        private String country;
        private int population;
        private int timezone;
        private long sunrise;
        private long sunset;
    }

    @Getter
    @Setter
    public static class Coord {
        private double lat;
        private double lon;
    }
}
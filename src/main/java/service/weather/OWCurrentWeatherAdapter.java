package service.weather;

import service.weather.enums.TimeOfDay;
import service.weather.enums.WeatherCondition;
import service.weather.factory.OpenWeather.OWCurrentWeather;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class OWCurrentWeatherAdapter implements CurrentWeather {
    private Long locationId;
    private final OWCurrentWeather ow;

    public OWCurrentWeatherAdapter(OWCurrentWeather currentWeather){
        this.ow = currentWeather;
    }

    @Override
    public Long getLocationId(){
        return this.locationId;
    }

    @Override
    public void setLocationId(Long id){
        this.locationId = id;
    }

    @Override
    public String getName() {
        return ow.getName();
    }

    @Override
    public Timestamp getDate() {
        var weatherDt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(ow.getDt()), ZoneId.systemDefault());
        return Timestamp.valueOf(weatherDt);
    }

    @Override
    public TimeOfDay getTimeOfDay() {
        var weatherDt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(ow.getDt()), ZoneId.systemDefault());
        return TimeOfDay.getTimeOfDayForTime(weatherDt);
    }

    @Override
    public double getTemperature() {
        return ow.getMain().getTemp();
    }

    @Override
    public double getFeelsLikeTemp() {
        return ow.getMain().getFeelsLike();
    }

    @Override
    public String getState() {
        return ow.getWeather().getFirst().getMain();
    }

    @Override
    public WeatherCondition getCondition() {
        return WeatherCondition.getWeatherConditionForCode(
                ow.getWeather().getFirst().getId()
        );
    }

    @Override
    public double getTempMin() {
        return ow.getMain().getTempMin();
    }

    @Override
    public double getTempMax() {
        return ow.getMain().getTempMax();
    }

    @Override
    public int getClouds() {
        return ow.getClouds().getAll();
    }

    @Override
    public int getHumidity() {
        return ow.getMain().getHumidity();
    }

    @Override
    public int getWindDeg() {
        return ow.getWind().getDeg();
    }

    @Override
    public double getWindSpeed() {
        return ow.getWind().getSpeed();
    }

    @Override
    public double getPressure() {
        return ow.getMain().getPressure();
    }

    @Override
    public Timestamp getSunrise() {
        var weatherSunrise = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(ow.getSys().getSunrise()), ZoneId.systemDefault());
        return Timestamp.valueOf(weatherSunrise);

    }

    @Override
    public Timestamp getSunset() {
        var weatherSunset = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(ow.getSys().getSunset()), ZoneId.systemDefault());
        return Timestamp.valueOf(weatherSunset);
    }

    @Override
    public Double getLatitude() {
        return ow.getCoord().getLat();
    }

    @Override
    public Double getLongitude() {
        return ow.getCoord().getLon();
    }
}

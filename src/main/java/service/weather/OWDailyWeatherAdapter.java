package service.weather;

import service.weather.enums.TimeOfDay;
import service.weather.enums.WeatherCondition;
import service.weather.factory.OpenWeather.OWDailyWeather;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class OWDailyWeatherAdapter implements DailyWeather {
    private final OWDailyWeather ow;

    public OWDailyWeatherAdapter(OWDailyWeather dailyWeather){
        this.ow = dailyWeather;
    }

    @Override
    public String getName() {
        return ow.getCity().getName();
    }

    @Override
    public List<DailyWeather.Forecast> getHourlyForecast() {
        List<DailyWeather.Forecast> hourlyForecast = new LinkedList<>();

        for (var forecast : ow.getList()) {
            var forecastAdapter = new ForecastAdapter(forecast);
            if (hourlyForecast.size() < 5) {
                hourlyForecast.add(forecastAdapter);
            }
        }

        return hourlyForecast;
    }

    @Override
    public List<DailyWeather.Forecast> getDailyForecast() {
        List<DailyWeather.Forecast> dailyForecast = new LinkedList<>();

        for(var forecast : ow.getList()){
            var forecastAdapter = new ForecastAdapter(forecast);

            var itHas = dailyForecast.stream().anyMatch(x -> x.getDate().toLocalDateTime().getDayOfMonth() ==
                    forecastAdapter.getDate().toLocalDateTime().getDayOfMonth());

            if(!itHas && forecastAdapter.getTimeOfDay() == TimeOfDay.DAY)
                dailyForecast.add(forecastAdapter);
        }

        return dailyForecast;
    }

    public static class ForecastAdapter implements DailyWeather.Forecast {
        private final OWDailyWeather.Forecast fc;

        public ForecastAdapter(OWDailyWeather.Forecast forecast){
            this.fc = forecast;
        }

        @Override
        public Timestamp getDate() {
            var weatherDt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(fc.getDt()), ZoneId.systemDefault());
            return Timestamp.valueOf(weatherDt);
        }

        @Override
        public double getTemperature() {
            return fc.getMain().getTemp();
        }

        @Override
        public double getMaxTemp() {
            return fc.getMain().getTempMax();
        }

        @Override
        public double getMinTemp() {
            return fc.getMain().getTempMin();
        }

        @Override
        public TimeOfDay getTimeOfDay() {
            var weatherDt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(fc.getDt()), ZoneId.systemDefault());
            return TimeOfDay.getTimeOfDayForTime(weatherDt);
        }

        @Override
        public WeatherCondition getCondition() {
            return WeatherCondition.getWeatherConditionForCode(fc.getWeather().getFirst().getId());
        }

        @Override
        public String getState() {
            return fc.getWeather().getFirst().getDescription();
        }

        @Override
        public double getWindSpeed() {
            return fc.getWind().getSpeed();
        }

        @Override
        public int getWindDeg() {
            return fc.getWind().getDeg();
        }

        @Override
        public double getPrecipitation() {
            return fc.getWeather().getFirst().getMain().equals("Rain")
                    ? fc.getRain().getRain3h() : fc.getWeather().getFirst().getMain().equals("Snow")
                    ? fc.getSnow().getSnow3h() : 0;
        }

        @Override
        public double getPressure() {
            return fc.getMain().getPressure();
        }

        @Override
        public int getClouds() {
            return fc.getClouds().getAll();
        }
    }

}

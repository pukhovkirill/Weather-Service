package controller;

import dao.LocationDAO;
import dao.repository.LocationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.thymeleaf.context.WebContext;
import service.WeatherApiClientService;
import service.weather.DailyWeather;
import service.weather.enums.TimeOfDay;
import service.weather.enums.WeatherCondition;
import service.weather.factory.OpenWeatherFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class WeatherForecastController extends BaseController{
    private static final String USERS_WEATHER_FORECASTS = "daily_weather";
    private final WeatherApiClientService weatherService;
    private final LocationDAO locationDAO;

    public WeatherForecastController(){
        final var weatherFactory = new OpenWeatherFactory();
        this.weatherService = new WeatherApiClientService(weatherFactory);

        this.locationDAO = new LocationRepository();
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var locationId = Long.parseLong(req.getParameter("location_id"));

        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());

        var dailyWeather = findForecast(locationId);

        if(dailyWeather == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Forecast or location not found");
            return;
        }

        setVariables(ctx, true, dailyWeather);
        templateEngine.process("forecast", ctx, writer);
    }

    protected void setVariables(WebContext ctx, boolean isUserAuthorized, ReducedWeather forecast) {
        super.setVariables(ctx, isUserAuthorized);
        ctx.setVariable(USERS_WEATHER_FORECASTS, forecast);
    }

    private ReducedWeather findForecast(Long locationId){
        var optionalLocation = this.locationDAO.find(locationId);

        if(optionalLocation.isEmpty())
            return null;

        var location = optionalLocation.get();
        var reduceDailyWeather = new ReducedWeather();
        reduceDailyWeather.fillReduce(this.weatherService.getDailyWeather(location));

        return reduceDailyWeather;
    }

    @Getter
    public static class ReducedWeather{
        private String name;
        private List<Forecast> hourlyForecast;
        private List<Forecast> dailyForecast;

        public void fillReduce(DailyWeather dailyWeather){
            this.name = dailyWeather.getCity().getName();
            this.hourlyForecast = new LinkedList<>();
            this.dailyForecast = new LinkedList<>();

            for(var item : dailyWeather.getList()){
                var forecast = new Forecast();

                var weatherDt = LocalDateTime.ofInstant(Instant.ofEpochSecond(item.getDt()), ZoneId.systemDefault());
                forecast.date = Timestamp.valueOf(weatherDt);
                forecast.temperature = item.getMain().getTemp();
                forecast.maxTemp = item.getMain().getTempMax();
                forecast.minTemp = item.getMain().getTempMin();
                forecast.timeOfDay = TimeOfDay.getTimeOfDayForTime(weatherDt);
                forecast.condition = WeatherCondition.getWeatherConditionForCode(item.getWeather().getFirst().getId());
                forecast.state = item.getWeather().getFirst().getDescription();
                forecast.windSpeed = item.getWind().getSpeed();
                forecast.windDeg = item.getWind().getDeg();
                forecast.precipitation =
                        forecast.state.equals("Rain") ? item.getRain().getRain3h() :
                        forecast.state.equals("Snow") ? item.getSnow().getSnow3h() : 0;
                forecast.pressure = item.getMain().getPressure();
                forecast.clouds = item.getClouds().getAll();

                if(hourlyForecast.size() < 5){
                    hourlyForecast.add(forecast);
                }else{
                    boolean itHas = dailyForecast.stream().anyMatch(x -> x.date.toLocalDateTime().getDayOfMonth() ==
                                                           forecast.date.toLocalDateTime().getDayOfMonth());
                    if(!itHas && forecast.timeOfDay == TimeOfDay.DAY)
                        dailyForecast.add(forecast);
                }
            }
        }

        @Getter
        public static class Forecast {
            private Timestamp date;
            private double temperature;
            private double maxTemp;
            private double minTemp;
            private TimeOfDay timeOfDay;
            private WeatherCondition condition;
            private String state;
            private double windSpeed;
            private int windDeg;
            private double precipitation;
            private double pressure;
            private int clouds;
        }
    }
}

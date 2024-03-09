package controller;

import dao.repository.LocationRepository;
import dao.repository.UserRepository;
import entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.thymeleaf.context.WebContext;
import service.LocationsManageService;
import service.WeatherApiClientService;
import service.weather.CurrentWeather;
import service.weather.enums.TimeOfDay;
import service.weather.enums.WeatherCondition;
import service.weather.factory.OpenWeatherFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class HomeController extends BaseController{
    protected static final String USERS_WEATHER_FORECASTS = "forecasts";

    private final WeatherApiClientService weatherService;
    private final LocationsManageService locationsManageService;

    public HomeController(){
        final var weatherFactory = new OpenWeatherFactory();
        this.weatherService = new WeatherApiClientService(weatherFactory);

        final var userDao = new UserRepository();
        final var locationDao = new LocationRepository();
        this.locationsManageService = new LocationsManageService(userDao, locationDao);
    }


    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(isUserAuthorized(req))
            authorizedProcess(engine, req);
        else
            unauthorizedProcess(engine);
    }

    private void authorizedProcess(ThymeleafTemplateEngine engine, HttpServletRequest req){
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());

        var session = req.getSession();
        var user = (User) session.getAttribute("user");

        var forecasts = foundUsersWeatherForecasts(user.getId());

        setVariables(ctx, true, forecasts);
        templateEngine.process("index", ctx, writer);
    }

    private void unauthorizedProcess(ThymeleafTemplateEngine engine){
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());

        setVariables(ctx, false, new LinkedList<>());
        templateEngine.process("index", ctx, writer);
    }

    protected void setVariables(WebContext ctx, boolean isUserAuthorized, List<ReducedCurrentWeather> forecast) {
        super.setVariables(ctx, isUserAuthorized);
        ctx.setVariable(USERS_WEATHER_FORECASTS, forecast);
    }


    private List<ReducedCurrentWeather> foundUsersWeatherForecasts(Long id){
        List<ReducedCurrentWeather> forecasts = new LinkedList<>();

        var locations = this.locationsManageService.getUserFavoritesLocations(id);

        for(var location : locations){
            var forecast = this.weatherService.getCurrentWeather(location);
            var reduceForecast = new ReducedCurrentWeather();
            reduceForecast.fillReduce(location.getId(), forecast);
            forecasts.add(reduceForecast);
        }

        return forecasts;
    }

    @Getter
    public static class ReducedCurrentWeather{
        private Long locationId;
        private String name;
        private Timestamp date;
        private TimeOfDay timeOfDay;
        private double temperature;
        private double feelsLikeTemp;
        private String state;
        private WeatherCondition condition;
        private double tempMin;
        private double tempMax;
        private int clouds;
        private int humidity;
        private int windDeg;
        private double windSpeed;
        private double pressure;
        private Timestamp sunrise;
        private Timestamp sunset;
        private Double latitude;
        private Double longitude;

        public void fillReduce(Long locationId, CurrentWeather weather){
            this.locationId = locationId;

            var weatherDt = LocalDateTime.ofInstant(Instant.ofEpochSecond(weather.getDt()), ZoneId.systemDefault());
            var weatherSunrise = LocalDateTime.ofInstant(Instant.ofEpochSecond(weather.getSys().getSunrise()), ZoneId.systemDefault());
            var weatherSunset = LocalDateTime.ofInstant(Instant.ofEpochSecond(weather.getSys().getSunset()), ZoneId.systemDefault());

            this.name = weather.getName();
            this.date = Timestamp.valueOf(weatherDt);
            this.timeOfDay = TimeOfDay.getTimeOfDayForTime(weatherDt);
            this.temperature = weather.getMain().getTemp();
            this.feelsLikeTemp = weather.getMain().getFeelsLike();
            this.state = weather.getWeather().getFirst().getMain();
            this.condition = WeatherCondition.getWeatherConditionForCode(weather.getWeather().getFirst().getId());
            this.tempMin = weather.getMain().getTempMin();
            this.tempMax = weather.getMain().getTempMax();
            this.clouds = weather.getClouds().getAll();
            this.humidity = weather.getMain().getHumidity();
            this.windDeg = weather.getWind().getDeg();
            this.windSpeed = weather.getWind().getSpeed();
            this.pressure = weather.getMain().getPressure();
            this.sunrise = Timestamp.valueOf(weatherSunrise);
            this.sunset = Timestamp.valueOf(weatherSunset);

            this.latitude = weather.getCoord().getLat();
            this.longitude = weather.getCoord().getLon();
        }
    }
}

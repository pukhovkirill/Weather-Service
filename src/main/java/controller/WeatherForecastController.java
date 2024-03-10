package controller;

import dao.LocationDAO;
import dao.repository.LocationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import service.WeatherApiClientService;
import service.weather.DailyWeather;
import service.weather.factory.FactoryManager;
import utility.PropertiesUtility;

public class WeatherForecastController extends BaseController{
    private static final String USERS_WEATHER_FORECASTS = "daily_weather";
    private final WeatherApiClientService weatherService;
    private final LocationDAO locationDAO;

    public WeatherForecastController(){
        String factoryProvider = PropertiesUtility.getApplicationProperty("weather.weather_provider");
        final var weatherFactory = FactoryManager.getWeatherProvider(factoryProvider);
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

        setVariables(ctx, dailyWeather);
        templateEngine.process("forecast", ctx, writer);
    }

    protected void setVariables(WebContext ctx, DailyWeather forecast) {
        super.setVariables(ctx, true);
        ctx.setVariable(USERS_WEATHER_FORECASTS, forecast);
    }

    private DailyWeather findForecast(Long locationId){
        var optionalLocation = this.locationDAO.find(locationId);

        if(optionalLocation.isEmpty())
            return null;

        var location = optionalLocation.get();
        return this.weatherService.getDailyWeather(location);
    }
}

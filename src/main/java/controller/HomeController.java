package controller;

import dao.repository.LocationRepository;
import dao.repository.UserRepository;
import entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import service.LocationsManageService;
import service.WeatherApiClientService;
import service.weather.CurrentWeather;
import service.weather.factory.FactoryManager;
import utility.PropertiesUtility;

import java.util.LinkedList;
import java.util.List;

public class HomeController extends BaseController{
    protected static final String USERS_WEATHER_FORECASTS = "forecasts";

    private final WeatherApiClientService weatherService;
    private final LocationsManageService locationsManageService;

    public HomeController(){
        String weatherProvider = PropertiesUtility.getApplicationProperty("weather.weather_provider");
        final var weatherFactory = FactoryManager.getWeatherProvider(weatherProvider);
        this.weatherService = new WeatherApiClientService(weatherFactory);

        final var userDao = new UserRepository();
        final var locationDao = new LocationRepository();
        this.locationsManageService = new LocationsManageService(userDao, locationDao);
    }


    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) {
        if(isUserAuthorized(req))
            authorizedProcess(engine, req);
        else
            unauthorizedProcess(engine);
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var location = req.getParameter("location_id");

        if(location == null || location.isBlank()){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found");
            return;
        }

        var locationId = Long.parseLong(location);
        var session = req.getSession();
        var user = (User) session.getAttribute("user");

        var result = locationsManageService.removeUserFavoritesLocation(user.getId(), locationId);

        if(!result){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.sendRedirect("/");
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

    protected void setVariables(WebContext ctx, boolean isUserAuthorized, List<CurrentWeather> forecast) {
        super.setVariables(ctx, isUserAuthorized);
        ctx.setVariable(USERS_WEATHER_FORECASTS, forecast);
    }


    private List<CurrentWeather> foundUsersWeatherForecasts(Long id){
        List<CurrentWeather> forecasts = new LinkedList<>();

        var locations = this.locationsManageService.getUserFavoritesLocations(id);

        for(var location : locations){
            var forecast = this.weatherService.getCurrentWeather(location);
            forecast.setLocationId(location.getId());
            forecasts.add(forecast);
        }

        return forecasts;
    }
}

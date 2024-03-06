package controller;

import dao.repository.LocationRepository;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import service.LocationsManageService;
import service.WeatherApiClientService;
import service.weather.CurrentWeather;
import service.weather.factory.OpenWeatherFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HomeController implements MappingController{
    private static final String IS_USER_AUTHORIZED_VARIABLE = "is_user_authorized";
    private static final String USERS_WEATHER_FORECASTS = "forecasts";

    private final WeatherApiClientService weatherService;
    private final LocationsManageService locationsManageService;

    public HomeController(){
        final var weatherFactory = new OpenWeatherFactory();
        this.weatherService = new WeatherApiClientService(weatherFactory);

        final var locationDao = new LocationRepository();
        this.locationsManageService = new LocationsManageService(locationDao);
    }


    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(isUserAuthorized(req))
            authorizedProcess(engine, req, resp);
        else
            unauthorizedProcess(engine, req, resp);
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(isUserAuthorized(req)){
            searchProcess(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "To use search you must log in");
    }

    private void authorizedProcess(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp){
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());

        var session = req.getSession();
        var user = (User) session.getAttribute("user");

        var forecasts = foundUsersWeatherForecasts(user);

        setVariables(ctx, true, forecasts);
        templateEngine.process("index", ctx, writer);
    }

    private void unauthorizedProcess(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp){
        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());

        setVariables(ctx, false, new LinkedList<>());
        templateEngine.process("index", ctx, writer);
    }

    private void searchProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        var wantedLocation = req.getParameter("wanted");

        if(wantedLocation == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found");
            return;
        }

        resp.sendRedirect(String.format("/search?wanted=%s", wantedLocation));
    }

    private void setVariables(WebContext ctx, boolean isUserAuthorized, List<CurrentWeather> forecasts){
        ctx.setVariable(IS_USER_AUTHORIZED_VARIABLE, isUserAuthorized);
        ctx.setVariable(USERS_WEATHER_FORECASTS, forecasts);
    }

    private boolean isUserAuthorized(HttpServletRequest req){
        var session = req.getSession();
        return session.getAttribute("user") != null;
    }

    private List<CurrentWeather> foundUsersWeatherForecasts(User user){
        List<CurrentWeather> forecasts = new LinkedList<>();

        var locations = this.locationsManageService.getUserFavoritesLocations(user);

        for(var location : locations){
            var forecast = this.weatherService.getCurrentWeather(location);
            forecasts.add(forecast);
        }

        return forecasts;
    }
}

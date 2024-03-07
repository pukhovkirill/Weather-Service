package controller;

import dao.repository.LocationRepository;
import dao.repository.UserRepository;
import entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Coordinates;
import org.thymeleaf.context.WebContext;
import service.LocationsManageService;

import java.util.Arrays;
import java.util.List;


public class SearchController extends BaseController{
    private static final String FOUND_SUITABLE_LOCATIONS = "suitable_locations";
    private static final String WANTED_LOCATION = "wanted_location";
    private final LocationsManageService locationsService;

    public SearchController(){
        final var userDao = new UserRepository();
        final var locationDao = new LocationRepository();
        this.locationsService = new LocationsManageService(userDao, locationDao);
    }

    @Override
    public void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var location = req.getParameter("wanted");

        if(location == null || location.isBlank()){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found");
            return;
        }

        var templateEngine = engine.getTemplateEngine();
        var webExchange = engine.getWebExchange();
        var writer = engine.getWriter();

        var ctx = new WebContext(webExchange, webExchange.getLocale());
        var suitableLocations = Arrays.asList(findSuitableLocations(location));

        setVariables(ctx, isUserAuthorized(req), suitableLocations, location);
        templateEngine.process("search", ctx, writer);
    }

    @Override
    public void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        var coordinates = buildCoordinates(req);

        var optionalLocation = locationsService.getOrPersist(coordinates);

        if(optionalLocation.isEmpty()){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        var location = optionalLocation.get();
        var user = (User) req.getSession().getAttribute("user");

        this.locationsService.addLocationToUsersFavorites(user.getId(), location);
        resp.sendRedirect("/");
    }

    private Coordinates buildCoordinates(HttpServletRequest req){
        var name = req.getParameter("location_name");
        var country = req.getParameter("location_country");
        var state = req.getParameter("location_state");
        var latitude = req.getParameter("location_latitude");
        var longitude = req.getParameter("location_longitude");

        var coordinates = new Coordinates();
        coordinates.setName(name);
        coordinates.setCountry(country);
        coordinates.setState(state);
        coordinates.setLatitude(Double.parseDouble(latitude));
        coordinates.setLongitude(Double.parseDouble(longitude));

        return coordinates;
    }

    private Coordinates[] findSuitableLocations(String location){
        return this.locationsService.find(location);
    }

    protected void setVariables(WebContext ctx, boolean isUserAuthorized, List<Coordinates> locations, String wantedLocation) {
        super.setVariables(ctx, isUserAuthorized);
        ctx.setVariable(FOUND_SUITABLE_LOCATIONS, locations);
        ctx.setVariable(WANTED_LOCATION, wantedLocation);
    }
}

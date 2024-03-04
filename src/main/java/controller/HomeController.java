package controller;

import dao.repository.SessionRepository;
import dao.repository.UserRepository;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import service.AuthorizationService;
import service.WeatherApiClientService;
import service.encryption.AESEncryptionService;
import service.weather.factory.OpenWeatherFactory;

import java.io.Writer;

public class HomeController implements MappingController{

    private AuthorizationService authorizationService;
    private WeatherApiClientService apiClientService;

    public HomeController(){
        final var userDAO = new UserRepository();
        final var sessionDAO = new SessionRepository();
        final var encryptionMethod = new AESEncryptionService();
        this.authorizationService = new AuthorizationService(userDAO, sessionDAO, encryptionMethod);

        final var weatherApi = new OpenWeatherFactory();
        this.apiClientService = new WeatherApiClientService(weatherApi);
    }

    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());


        templateEngine.process("home", ctx, writer);
    }
}

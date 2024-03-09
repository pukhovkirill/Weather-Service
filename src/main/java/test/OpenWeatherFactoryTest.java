package test;

import org.junit.jupiter.api.Test;
import service.weather.CurrentWeather;
import service.weather.factory.OpenWeatherFactory;

public class OpenWeatherFactoryTest {

    @Test
    void getWeatherForecastTest(){
        var factory = new OpenWeatherFactory();
    }
}

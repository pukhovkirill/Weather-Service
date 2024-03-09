package service.weather.factory;

import java.util.HashMap;
import java.util.Map;

public class FactoryManager {
    private static final Map<String, WeatherFactory> factories;

    static{
        factories = new HashMap<>();
        factories.put("openweather", new OpenWeatherFactory());
    }

    public static WeatherFactory getWeatherProvider(String providerName){
        return factories.get(providerName.toLowerCase());
    }
}

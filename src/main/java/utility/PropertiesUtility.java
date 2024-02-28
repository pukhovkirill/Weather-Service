package utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtility {
    private static Properties weatherProperties;

    public static String getOpenWeatherApiKey(){
        return getProperty("weather.ow_api");
    }

    public static String getOpenWeatherUri(){
        return getProperty("weather.ow_weather_uri");
    }

    public static String getOpenWeatherGeoLocationUri(){
        return getProperty("weather.ow_geo_location_uri");
    }

    private static String getProperty(String key){
        if(weatherProperties == null)
            readWeatherProperties();

        return weatherProperties.getProperty(key);
    }

    private static void readWeatherProperties(){
        try{
            weatherProperties = new Properties();
            var fis = new FileInputStream("src/main/resources/weather.properties");
            weatherProperties.load(fis);
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

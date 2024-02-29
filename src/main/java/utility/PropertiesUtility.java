package utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtility {
    private static Properties weatherProperties;
    private static Properties applicationProperties;

    public static String getOpenWeatherApiKey(){
        return getWeatherProperty("weather.ow_api");
    }

    public static String getOpenWeatherUri(){
        return getWeatherProperty("weather.ow_weather_uri");
    }

    public static String getOpenWeatherGeoLocationUri(){
        return getWeatherProperty("weather.ow_geo_location_uri");
    }

    public static String getKeyStoragePassword(){
        return getApplicationProperty("app.key_storage_password");
    }

    private static String getWeatherProperty(String key){
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

    private static String getApplicationProperty(String key){
        if(applicationProperties == null)
            readApplicationProperties();

        return applicationProperties.getProperty(key);
    }

    private static void readApplicationProperties(){
        try{
            weatherProperties = new Properties();
            var fis = new FileInputStream("src/main/resources/app.properties");
            weatherProperties.load(fis);
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

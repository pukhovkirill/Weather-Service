package utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtility {
    private static Properties applicationProperties;

    public static String getApplicationProperty(String key){
        if(applicationProperties == null)
            readApplicationProperties();

        return applicationProperties.getProperty(key);
    }

    private static void readApplicationProperties(){
        try{
            applicationProperties = new Properties();
            var fis = new FileInputStream("/home/yukir/IdeaProjects/Weather-Service/src/main/resources/app.properties");
            applicationProperties.load(fis);
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

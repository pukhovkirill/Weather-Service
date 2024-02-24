package utility;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class HibernateUtility {

    private static SessionFactory factory;

    private static SessionFactory buildSessionFactory(){
        try{
            var configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            return configuration.buildSessionFactory();
        }catch(Throwable ex){
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory(){
        if(factory == null)
            factory = buildSessionFactory();

        return factory;
    }
}

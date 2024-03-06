package dao.repository;

import dao.LocationDAO;
import entity.Location;
import jakarta.persistence.NoResultException;
import org.hibernate.query.Query;
import utility.HibernateUtility;

import java.util.Optional;

public class LocationRepository implements LocationDAO {

    @Override
    public Optional<Location> find(Long id) {
        var session = HibernateUtility.getSessionFactory().openSession();

        Location location = null;

        try{
            session.beginTransaction();

            location = session.find(Location.class, id);
            location.getUsers().iterator();

            session.getTransaction().commit();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(location);
    }

    @Override
    public Optional<Location>  findByCoordinates(Double latitude, Double longitude) {
        var session = HibernateUtility.getSessionFactory().openSession();

        Location location = null;

        String hql = "select l from locations l where l.latitude = :latitude and l.longitude = :longitude";
        Query<Location> query = session.createQuery(hql, Location.class);

        try{
            query.setParameter("latitude", latitude);
            query.setParameter("longitude", longitude);

            location = query.getSingleResult();
            if(location != null)
                location.getUsers().iterator();
        }catch (NoResultException nre){
            return Optional.empty();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(location);
    }

    @Override
    public Optional<Location>  findByName(String name) {
        var session = HibernateUtility.getSessionFactory().openSession();

        Location location = null;

        String hql = "select l from locations l where l.name = :name";
        Query<Location> query = session.createQuery(hql, Location.class);

        try{
            query.setParameter("name", name);
            location = query.getSingleResult();
            if(location != null)
                location.getUsers().iterator();
        }catch (NoResultException nre){
            return Optional.empty();
        }catch (RuntimeException ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(location);
    }

    @Override
    public void update(Location entity) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var location = session.find(Location.class, entity.getId());
            location.setName(entity.getName());
            location.setUsers(entity.getUsers());
            location.setLatitude(location.getLatitude());
            location.setLongitude(location.getLongitude());

            session.getTransaction().commit();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

    @Override
    public void delete(Long id) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var location = session.find(Location.class, id);
            if(location != null)
                session.remove(location);

            session.getTransaction().commit();
        }catch (RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }
}

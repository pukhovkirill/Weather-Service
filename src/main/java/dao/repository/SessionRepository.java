package dao.repository;

import dao.SessionDAO;
import entity.Session;
import entity.User;
import jakarta.persistence.NoResultException;
import org.hibernate.query.Query;
import utility.HibernateUtility;

import java.util.Optional;
import java.util.UUID;

public class SessionRepository implements SessionDAO {
    @Override
    public Optional<Session> find(UUID uuid) {
        var session = HibernateUtility.getSessionFactory().openSession();

        Session userSession = null;

        try{
            session.beginTransaction();

            userSession = session.find(Session.class, uuid);
            if(userSession != null)
                userSession.getUser().getLocations().iterator();

            session.getTransaction().commit();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(userSession);
    }

    @Override
    public Optional<Session> findByUser(User user) {
        var session = HibernateUtility.getSessionFactory().openSession();

        Session userSession = null;

        String hql = "select s from sessions s where s.user.id = :id";
        Query<Session> query = session.createQuery(hql, Session.class);

        try{
            query.setParameter("id", user.getId());
            userSession = query.getSingleResult();
            if(userSession != null)
                userSession.getUser().getLocations().iterator();
        }catch (NoResultException nre){
            return Optional.empty();
        }catch (RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(userSession);
    }

    @Override
    public void update(Session entity) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var userSession = session.find(Session.class, entity.getUuid());
            userSession.setUser(entity.getUser());
            userSession.setExpiresAt(entity.getExpiresAt());

            session.getTransaction().commit();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

    @Override
    public void delete(UUID uuid) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var userSession = session.find(Session.class, uuid);
            if(userSession != null)
                session.remove(userSession);

            session.getTransaction().commit();
        }catch (RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }
}

package dao.repository;

import dao.SessionDAO;
import entity.Session;
import entity.User;
import org.hibernate.query.Query;
import utility.HibernateUtility;

import java.util.Optional;

public class SessionRepository implements SessionDAO {
    @Override
    public Optional<Session> find(Long id) {
        var session = HibernateUtility.getSessionFactory().openSession();

        Session userSession = null;

        try{
            session.beginTransaction();

            userSession = session.find(Session.class, id);

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

        String hql = "select s from sessions s where s.user.id like :id";
        Query<Session> query = session.createQuery(hql, Session.class);

        try{
            query.setParameter("id", user.getId());
            userSession = query.getSingleResult();
        }catch (RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(userSession);
    }

    @Override
    public void save(Session entity) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            session.persist(entity);

            session.getTransaction().commit();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

    @Override
    public void update(Session entity) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var userSession = session.find(Session.class, entity.getId());
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
    public void delete(Long id) {
        var session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var userSession = session.find(Session.class, id);
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

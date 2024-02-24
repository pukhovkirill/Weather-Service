package dao.postgresql;

import dao.UserDAO;
import entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utility.HibernateUtility;

import java.util.Optional;

public class UserRepository implements UserDAO {

    @Override
    public Optional<User> find(Long id) {
        Session session = HibernateUtility.getSessionFactory().openSession();

        User user = null;

        try{
            session.beginTransaction();

            user = session.find(User.class, id);

            session.getTransaction().commit();
        }catch(RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Session session = HibernateUtility.getSessionFactory().openSession();

        User user = null;

        String hql = "select u from users u where u.login like :login";
        Query<User> query = session.createQuery(hql, User.class);

        try{
            query.setParameter("login", login);
            user = query.getSingleResult();
        }catch (RuntimeException ex){
            ex.printStackTrace();
        }finally {
            session.close();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public void save(User entity) {
        Session session = HibernateUtility.getSessionFactory().openSession();

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
    public void update(User entity) {
        Session session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var user = session.find(User.class, entity.getId());
            user.setLogin(entity.getLogin());
            user.setPassword(entity.getPassword());

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
        Session session = HibernateUtility.getSessionFactory().openSession();

        try{
            session.beginTransaction();

            var user = session.find(User.class, id);
            if(user != null)
                session.remove(user);

            session.getTransaction().commit();
        }catch (RuntimeException ex){
            session.getTransaction().rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }
}

package dao;

import utility.HibernateUtility;

import java.util.Optional;

public interface BaseDAO<T> {
    Optional<T> find(Long id);
    default void save(T entity){
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
    void update(T entity);
    void delete(Long id);
}

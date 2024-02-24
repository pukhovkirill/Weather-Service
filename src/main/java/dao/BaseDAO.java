package dao;

import java.util.Optional;

public interface BaseDAO<T> {
    Optional<T> find(Long id);
    void save(T entity);
    void update(T entity);
    void delete(Long id);
}

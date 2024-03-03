package dao;

import entity.Session;
import entity.User;

import java.util.Optional;
import java.util.UUID;

public interface SessionDAO extends BaseDAO<Session> {

    default Optional<Session> find(Long id){
        return Optional.empty();
    }

    Optional<Session> find(UUID uuid);

    Optional<Session> findByUser(User user);
}

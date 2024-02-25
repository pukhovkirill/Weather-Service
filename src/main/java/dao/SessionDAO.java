package dao;

import entity.Session;
import entity.User;

import java.util.Optional;

public interface SessionDAO extends BaseDAO<Session> {

    Optional<Session> findByUser(User user);
}

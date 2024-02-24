package dao;

import entity.User;

import java.util.Optional;

public interface UserDAO extends BaseDAO<User> {

    Optional<User> findByLogin(String login);

}

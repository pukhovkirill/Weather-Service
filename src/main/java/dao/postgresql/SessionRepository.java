package dao.postgresql;

import dao.SessionDAO;
import entity.Session;

import java.util.Optional;

public class SessionRepository implements SessionDAO {
    @Override
    public Optional<Session> find(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Session entity) {

    }

    @Override
    public void update(Session entity) {

    }

    @Override
    public void delete(Long id) {

    }
}

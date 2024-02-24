package dao.postgresql;

import dao.LocationDAO;
import entity.Location;

import java.util.Optional;

public class LocationRepository implements LocationDAO {
    @Override
    public Optional<Location> find(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Location entity) {

    }

    @Override
    public void update(Location entity) {

    }

    @Override
    public void delete(Long id) {

    }
}

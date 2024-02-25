package dao;

import entity.Location;

import java.util.Optional;

public interface LocationDAO extends BaseDAO<Location> {

    Optional<Location> findByCoordinates(Double latitude, Double longitude);

    Optional<Location>  findByName(String name);
}

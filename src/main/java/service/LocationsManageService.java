package service;

import dao.LocationDAO;
import entity.Location;
import entity.User;
import model.Coordinates;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LocationsManageService {
    private final LocationDAO locationRepository;
    private final GeoLocationService geoLocationService;

    public LocationsManageService(LocationDAO locationRepository){
        this.locationRepository = locationRepository;
        this.geoLocationService = new GeoLocationService();
    }

    public Optional<Location> getOrPersist(Coordinates coordinates){
        Location location;

        var optionalLocation =
                this.locationRepository.findByCoordinates(
                        coordinates.getLatitude(),
                        coordinates.getLongitude()
                );

        if(optionalLocation.isEmpty()){
            location = new Location();
            location.setName(coordinates.getName());
            location.setUsers(new HashSet<>());
            location.setLatitude(coordinates.getLatitude());
            location.setLongitude(coordinates.getLongitude());

            locationRepository.save(location);

            return Optional.of(location);
        }else{
            return optionalLocation;
        }
    }

    public Coordinates[] find(String name){
        return this.geoLocationService.findCoordinatesByName(name);
    }

    //todo: check this code
    public void addLocationToUsersFavorites(Location location, User user){
        location.getUsers().add(user);
        user.getLocations().add(location);
    }

    //todo: check this code
    public Set<Location> getUserFavoritesLocations(User user){
        return user.getLocations();
    }
}

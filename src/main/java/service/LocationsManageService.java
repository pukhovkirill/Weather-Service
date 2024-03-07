package service;

import dao.LocationDAO;
import dao.UserDAO;
import dao.repository.UserRepository;
import entity.Location;
import model.Coordinates;

import java.util.*;

public class LocationsManageService {
    private final UserDAO userRepository;
    private final LocationDAO locationRepository;
    private final GeoLocationService geoLocationService;

    public LocationsManageService(UserRepository userRepository, LocationDAO locationRepository){
        this.userRepository = userRepository;
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
            location.setUsers(new LinkedList<>());
            location.setLatitude(coordinates.getLatitude());
            location.setLongitude(coordinates.getLongitude());

            locationRepository.save(location);
            return locationRepository.findByCoordinates(coordinates.getLatitude(), coordinates.getLongitude());
        }else{
            return optionalLocation;
        }
    }

    public Coordinates[] find(String name){
        return this.geoLocationService.findCoordinatesByName(name);
    }

    public void addLocationToUsersFavorites(Long id, Location location){
        var actualUser = this.userRepository.find(id);

        if(actualUser.isEmpty())
            return;

        var user = actualUser.get();

        if(user.getLocations().stream().anyMatch(x -> Objects.equals(x.getId(), location.getId()))) {
            return;
        }

        user.getLocations().add(location);

        this.userRepository.update(user);
    }

    public Collection<Location> getUserFavoritesLocations(Long id){
        var actualUser = this.userRepository.find(id);

        if(actualUser.isEmpty())
            return null;

        var user = actualUser.get();

        return user.getLocations();
    }

}

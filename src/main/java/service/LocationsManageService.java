package service;

import dao.LocationDAO;
import dao.repository.LocationRepository;
import entity.Location;
import entity.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LocationsManageService {

    private final LocationDAO locationRepository;

    public LocationsManageService(){
        this.locationRepository = new LocationRepository();
    }

    public Optional<Location> findOrPersist(String name){
        Location location = null;

        var optionalLocation = this.locationRepository.findByName(name);

        if(optionalLocation.isEmpty()){
            var geoService = new GeoLocationService();
            var coordinates = geoService.findCoordinatesByName(name);

            location = new Location();
            location.setName(name);
            location.setUsers(new HashSet<>());
            location.setLatitude(coordinates.getLatitude());
            location.setLongitude(coordinates.getLongitude());

            locationRepository.save(location);

            return Optional.of(location);
        }else{
            return optionalLocation;
        }
    }

    public Optional<Location> find(double latitude, double longitude){
        Location location = null;

        var optionLocation = this.locationRepository.findByCoordinates(latitude, longitude);

        if(optionLocation.isPresent())
            location = optionLocation.get();

        return Optional.ofNullable(location);
    }

    public void persistLocationForUser(Location location, User user){
        location.getUsers().add(user);
        user.getLocations().add(location);
    }

    public Set<Location> findLocationsForUser(User user){
        return user.getLocations();
    }
}

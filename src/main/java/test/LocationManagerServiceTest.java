package test;

import dao.repository.LocationRepository;
import dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LocationsManageService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationManagerServiceTest {
    private LocationsManageService locationsManageService;

    @BeforeEach
    void init(){
        var userRepository = new UserRepository();
        var locationRepository = new LocationRepository();
        this.locationsManageService = new LocationsManageService(userRepository, locationRepository);
    }

    @Test
    void findTest(){
        String testLocation = "London";
        var locations = locationsManageService.find(testLocation);

        assertEquals(testLocation, locations[0].getName());
    }

    @Test
    void getOrPersistTest(){
        var coordinates = locationsManageService.find("London")[0];
        var optionalLocation = locationsManageService.getOrPersist(coordinates);
        assertTrue(optionalLocation.isPresent());
    }

    @Test
    void addLocationToUsersFavoritesTest(){

    }

    @Test
    void getUserFavoritesLocationsTest(){

    }
}

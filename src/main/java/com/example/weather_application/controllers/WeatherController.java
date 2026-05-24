package com.example.weather_application.controllers;

import com.example.weather_application.location.Location;
import com.example.weather_application.location.LocationSearchFilter;
import com.example.weather_application.services.WeatherService;
import com.example.weather_application.user.User;
import com.example.weather_application.services.MainService;
import com.example.weather_application.user.UserSearchFilter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final MainService mainService;
    private final WeatherService weatherService;

    public WeatherController(MainService mainService, WeatherService weatherService) {
        this.mainService = mainService;
        this.weatherService = weatherService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> postUser (
            @RequestBody @Valid User user
    ) {
        log.info("Posting new user with user id " + user.userId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mainService.addUser(user));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findUserByFilter(
        @RequestParam(name = "id", required = false) Long id,
        @RequestParam(name = "userId", required = false) String userId,
        @RequestParam(name = "pageNum", required = false) Integer pageNum,
        @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        log.info("Finding users by filters");

        UserSearchFilter filter = new UserSearchFilter(
                id,
                userId,
                pageNum,
                pageSize
        );

        return ResponseEntity.ok().body(
                mainService.searchAllUsersByFilter(filter)
        );
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> findLocationByFilter(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "icon", required = false) String icon,
            @RequestParam(name = "temperature", required = false) Double temperature,
            @RequestParam(name = "humidity", required = false) Long humidity,
            @RequestParam(name = "windSpeed", required = false) String windSpeed,
            @RequestParam(name = "sunrise", required = false) Long sunrise,
            @RequestParam(name = "sunset", required = false) Long sunset,
            @RequestParam(name = "time", required = false) String time,
            @RequestParam(name = "pageNum", required = false) Integer pageNum,
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        log.info("Finding locations by filters");

        LocationSearchFilter filter = new LocationSearchFilter(
                id,
                name,
                country,
                description,
                icon,
                temperature,
                humidity,
                windSpeed,
                sunrise,
                sunset,
                time,
                pageNum,
                pageSize
        );

        return ResponseEntity.ok().body(
                mainService.searchAllLocationsByFilter(filter)
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser (
            @PathVariable Long id
    ) {
        log.info("Deleting user with id {}", id);
        return ResponseEntity.ok()
                .body(mainService.deleteUser(id));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation (
            @PathVariable Long id
    ) {
        log.info("Deleting location with id {}", id);
        return ResponseEntity.ok()
                .body(mainService.deleteLocation(id));
    }

    @PutMapping("/users/{userId}/locations")
    public ResponseEntity<Location> postLocation (
           @PathVariable Long userId,
           @RequestBody Location location
    ) {
        log.info("Posting location " + location.name() + " for user with id {}", userId);
        log.warn("Данные поступают только на ру раскладке, иначе баг !!!!!!! " + location.name());

        return ResponseEntity.ok()
                .body(weatherService.postLocationForUser(location.name(), userId));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser (
            @PathVariable("id") Long id,
            @RequestBody @Valid User user
    ) {
        log.info("Updating user with id {}", user.id());

        return ResponseEntity.ok()
                .body(mainService.updateUser(id, user));
    }

    @GetMapping("/info/locations")
    public ResponseEntity<Location> getLocation (
            @RequestParam String name
    ) {
        log.info("Getting location " + name);

        return ResponseEntity.ok()
                .body(mainService.getLocation(name));
    }

    @GetMapping("/users/{id}/locations")
    public ResponseEntity<List<Location>> getUserLocations (
            @PathVariable Long id
    ) {
        log.info("Getting locations for user with id {}", id);

        return ResponseEntity.ok()
                .body(mainService.findUserLocations(id));
    }

    @DeleteMapping("/users/{id}/locations")
    public ResponseEntity<Void> deleteUserLocations (
            @PathVariable Long id,
            @RequestParam String name
    ) {
        log.info("Deleting location " + name + " for user with id {}", id);

        return ResponseEntity.ok()
                .body(mainService.deleteLocationForUser(id, name));
    }

}

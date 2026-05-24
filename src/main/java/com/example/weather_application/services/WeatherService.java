package com.example.weather_application.services;

import com.example.weather_application.errors.NonExistentLocationNameException;
import com.example.weather_application.location.Location;
import com.example.weather_application.entities.LocationEntity;
import com.example.weather_application.errors.LocationIsAlreadyIncludedException;
import com.example.weather_application.mappers.LocationMapper;
import com.example.weather_application.repos.LocationRepository;
import com.example.weather_application.repos.UserRepository;
import com.example.weather_application.entities.UserEntity;
import com.example.weather_application.mappers.UserMapper;
import com.example.weather_application.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WeatherService {
    private final Logger log = LoggerFactory.getLogger(WeatherService.class);

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.base-url}")
    private String baseUrl;

    @Value("${openweather.defaults.units}")
    private String defaultUnits;

    @Value("${openweather.defaults.lang}")
    private String defaultLang;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    private final Utils utils;

    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherService(UserRepository userRepository, LocationRepository locationRepository, Utils utils, UserMapper userMapper, LocationMapper locationMapper) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.utils = utils;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
    }

    public LocationEntity getInfoLocation(
            String nameLocation
    ) {
        log.info("getInfoLocation call");

        String url = baseUrl + "/weather?q=" + nameLocation + "&appid=" + apiKey + "&units=" + defaultUnits + "&lang=" + defaultLang;

        ResponseEntity<Map> response;
        try {
            response = restTemplate.getForEntity(url, Map.class);
        } catch (Exception e) {
            throw new NonExistentLocationNameException("Could not get info location");
        }

        Map<String, Object> body = response.getBody();

        if (locationRepository.findByName(nameLocation).isPresent()) {
            LocationEntity location = locationRepository.findByName(nameLocation).get();

            location.setName(((String) body.get("name")).toLowerCase());
            location.setCountry((String) ((Map<String, Object>) body.get("sys")).get("country"));
            location.setDescription((String) ((Map<String, Object>) ((ArrayList) body.get("weather")).get(0)).get("description"));
            location.setIcon((String) ((Map<String, Object>) ((ArrayList) body.get("weather")).get(0)).get("icon"));
            location.setTemperature((Double) ((Map<String, Object>) body.get("main")).get("temp"));
            location.setHumidity(Long.valueOf((Integer) ((Map<String, Object>) body.get("main")).get("humidity")));
            location.setWindSpeed(String.valueOf(((Map<String, Object>) body.get("wind")).get("speed")));
            location.setSunrise(Long.valueOf((Integer) ((Map<String, Object>) body.get("sys")).get("sunrise")));
            location.setSunset(Long.valueOf((Integer) ((Map<String, Object>) body.get("sys")).get("sunset")));
            location.setTime(utils.calculateTheTimeInterval(
                    LocalDateTime.now().toString().split("T")[1].substring(0, 2)
            ));
            location.setLastUpdated(LocalDate.now());

            return location;
        } else {
            LocationEntity newLocation = new LocationEntity(
                    null,
                    ((String)body.get("name")).toLowerCase(),
                    (String) ((Map<String, Object>)body.get("sys")).get("country"),
                    (String) ((Map<String, Object>)((ArrayList)body.get("weather")).get(0)).get("description"),
                    (String) ((Map<String, Object>)((ArrayList)body.get("weather")).get(0)).get("icon"),
                    (Double) ((Map<String, Object>)body.get("main")).get("temp"),
                    Long.valueOf((Integer)((Map<String, Object>)body.get("main")).get("humidity")),
                    String.valueOf(((Map<String, Object>)body.get("wind")).get("speed")),
                    Long.valueOf((Integer)((Map<String, Object>)body.get("sys")).get("sunrise")),
                    Long.valueOf((Integer)((Map<String, Object>)body.get("sys")).get("sunset")),
                    utils.calculateTheTimeInterval(LocalDateTime.now()
                            .toString().split("T")[1].substring(0, 2)),
                    LocalDate.now()
            );

            return newLocation;
        }
    }

    @Transactional
    public Location postLocationForUser(
            String nameLocation,
            Long userId
    ) {
        log.info("postLocationForUser call");

        UserEntity oldUser = userRepository.findById(userId).orElseThrow(()->
                new NoSuchElementException("User Not Found"));

        nameLocation = nameLocation.toLowerCase();

        Optional<LocationEntity> existingLocation = locationRepository.findByName(nameLocation);
        if (existingLocation.isPresent() && oldUser.getLocationEntities().contains(existingLocation.get())) {
            throw new LocationIsAlreadyIncludedException("Location already exists");
        }

        if(locationRepository.findByName(nameLocation).isPresent()) {
            LocationEntity newLocation = locationRepository.findByName(nameLocation).get();

            oldUser.getLocationEntities().add(newLocation);

            userRepository.save(oldUser);

            return locationMapper.toDomain(newLocation);
        } else {
            LocationEntity newLocation = getInfoLocation(nameLocation);
            locationRepository.save(newLocation);

            oldUser.getLocationEntities().add(newLocation);
            userRepository.save(oldUser);

            return locationMapper.toDomain(newLocation);
        }
    }

}

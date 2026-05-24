package com.example.weather_application.services;

import com.example.weather_application.errors.UserAlreadyExistException;
import com.example.weather_application.location.Location;
import com.example.weather_application.entities.LocationEntity;
import com.example.weather_application.location.LocationSearchFilter;
import com.example.weather_application.mappers.LocationMapper;
import com.example.weather_application.repos.LocationRepository;
import com.example.weather_application.repos.UserRepository;
import com.example.weather_application.user.User;
import com.example.weather_application.entities.UserEntity;
import com.example.weather_application.mappers.UserMapper;
import com.example.weather_application.user.UserSearchFilter;
import com.example.weather_application.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MainService {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final WeatherService weatherService;

    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    private final Utils utils;

    private final Logger log = LoggerFactory.getLogger(MainService.class);

    public MainService(UserRepository userRepository, LocationRepository locationRepository, WeatherService weatherService, UserMapper userMapper, LocationMapper locationMapper, Utils utils) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.weatherService = weatherService;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
        this.utils = utils;
    }

    public User addUser(
            User user
    ) {
        log.info("Adding new user");

        if (userRepository.findByUserId(user.userId()).isPresent()) {
            throw new UserAlreadyExistException("User already exists");
        }

        UserEntity userEntity = new UserEntity(
                user.userId()
        );

        userRepository.save(userEntity);

        return userMapper.toDomain(userEntity);
    }

    public Void deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        log.info("Deleting user with id " + id);

        userRepository.deleteById(id);
        return null;
    }

    public Location getLocation(
            String nameLocation
    ) {
        log.info("Getting location with name " + nameLocation);

        LocationEntity location;
        if(locationRepository.findByName(nameLocation.toLowerCase()).isPresent()) {
            LocationEntity oldLocation = locationRepository.findByName(nameLocation.toLowerCase()).get();

            if(!utils.calculateTheTimeInterval(LocalDateTime.now()
                    .toString().split("T")[1].substring(0, 2)).equals(oldLocation.getTime()) || (utils.calculateTheTimeInterval(LocalDateTime.now()
                    .toString().split("T")[1].substring(0, 2)).equals(oldLocation.getTime()) && !Objects.equals(oldLocation.getLastUpdated(), LocalDate.now()))) {
                location = locationRepository.save(weatherService.getInfoLocation(nameLocation.toLowerCase()));
            } else {
                location = oldLocation;
            }
        } else {
            location = locationRepository
                    .save(weatherService.getInfoLocation(nameLocation.toLowerCase()));
        }

        return locationMapper.toDomain(location);
    }

    public Void deleteLocation(
            Long id
    ) {
        log.info("Deleting location with id " + id);

        if (locationRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Location with id " + id + " not found");
        }
        locationRepository.deleteById(id);
        return null;
    }

    public User updateUser(
            Long id,
            User user
    ) {
        log.info("Updating user with id " + id);

        Optional<UserEntity> userEntity1 = userRepository.findById(id);
        Optional<UserEntity> userEntity2 = userRepository.findByUserId(user.userId());
        if (userEntity1.isEmpty()) {
            throw new NoSuchElementException("User with id " + id + " not found");
        } else if (userEntity2.isPresent() && !userEntity1.get().getId().equals(userEntity2.get().getId())) {
            throw new UserAlreadyExistException("User id " + user.userId() + " already exists");
        }

        var updatedUser = userRepository.save(new UserEntity(
                id,
                user.userId()
        ));

        return userMapper.toDomain(updatedUser);
    }

    public List<User> searchAllUsersByFilter(
            UserSearchFilter filter
    ) {
        log.info("Searching all users with filter");

        int pageNum = filter.pageNum() == null ?
                0 : filter.pageNum();
        int pageSize = filter.pageSize() == null ?
                10 : filter.pageSize();

        var pageable = PageRequest.of(pageNum, pageSize);

        Page<UserEntity> pageResult = userRepository.findAllByFilter(
                filter.id(),
                filter.userId(),
                pageable
        );

        return pageResult.stream()
                .map(userMapper::toDomain)
                .toList();
    }

    public List<Location> searchAllLocationsByFilter(
            LocationSearchFilter filter
    ) {
        log.info("Searching all locations with filter");

        int pageNum = filter.pageNum() == null ?
                0 : filter.pageNum();
        int pageSize = filter.pageSize() == null ?
                10 : filter.pageSize();

        var pageable = PageRequest.of(pageNum, pageSize);

        Page<LocationEntity> pageResult = locationRepository.findAllByFilter(
                filter.id(),
                filter.name(),
                filter.country(),
                filter.description(),
                filter.icon(),
                filter.temperature(),
                filter.humidity(),
                filter.windSpeed(),
                filter.sunrise(),
                filter.sunset(),
                filter.time(),
                pageable
        );

        return pageResult.stream()
                .map(locationMapper::toDomain)
                .toList();
    }

    public List<Location> findUserLocations(
            Long id
    ) {
        log.info("Finding user locations with id " + id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

        List<LocationEntity> locationsEnt = user.getLocationEntities();
        if(locationsEnt.isEmpty()) {
            return Collections.emptyList();
        } else {
            return locationsEnt.stream()
                    .map(locationMapper::toDomain)
                    .toList();
        }
    }

    @Transactional
    public Void deleteLocationForUser(
            Long id,
            String name
    ) {
        log.info("Deleting location " + name + " for user with id " + id);

        name = name.toLowerCase();

        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isEmpty()) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        List<LocationEntity> locations = userEntity.get().getLocationEntities();

        Optional<LocationEntity> location = Optional.empty();
        for (LocationEntity loc : locations) {
            if (loc.getName().equals(name)) {
                location = Optional.of(loc);
            }
        }

        if (location.isEmpty()) {
            throw new NoSuchElementException("User is not has location with name " + name);
        } else {
            locations.remove(location.get());

            userRepository.save(userEntity.get());
        }

        return null;
    }

}

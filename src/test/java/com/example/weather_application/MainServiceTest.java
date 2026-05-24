package com.example.weather_application;

import com.example.weather_application.errors.NonExistentLocationNameException;
import com.example.weather_application.errors.UserAlreadyExistException;
import com.example.weather_application.location.Location;
import com.example.weather_application.entities.LocationEntity;
import com.example.weather_application.mappers.LocationMapper;
import com.example.weather_application.mappers.UserMapper;
import com.example.weather_application.repos.LocationRepository;
import com.example.weather_application.repos.UserRepository;
import com.example.weather_application.services.MainService;
import com.example.weather_application.services.WeatherService;
import com.example.weather_application.user.User;
import com.example.weather_application.entities.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MainServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LocationRepository locationRepository;

    @Mock
    WeatherService weatherService;

    @Mock
    UserMapper userMapper;
    @Mock
    LocationMapper locationMapper;

    @Mock
    Utils utils;

    @InjectMocks
    MainService mainService;

    @Test
    @DisplayName("успешное добавление, юзер еще не существует")
    void addUser_UserNotExist() {
        User userInput = new User(
                null,
                "Fofa"
        );
        UserEntity userEntity = new UserEntity(
                23L,
                "Fofa"
        );
        User userOutput = new User(
                23L,
                "Fofa"
        );

        Mockito.when(userRepository.findByUserId(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(userEntity);
        Mockito.when(userMapper.toDomain(Mockito.any(UserEntity.class)))
                .thenReturn(userOutput);

        User result = mainService.addUser(userInput);

        Assertions.assertEquals(userOutput, result);

        Mockito.verify(userRepository, Mockito.times(1)).findByUserId(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
        Mockito.verify(userMapper, Mockito.times(1)).toDomain(Mockito.any(UserEntity.class));
    }

    @Test
    @DisplayName("юзер существует, ошибка")
    void addUser_UserExists() {
        User userInput = new User(
                null,
                "Fofa"
        );
        UserEntity userEntity = new UserEntity(
                23L,
                "Fofa"
        );

        Mockito.when(userRepository.findByUserId(userInput.userId()))
                .thenReturn(Optional.of(userEntity));

        UserAlreadyExistException exception = Assertions.assertThrows(
                UserAlreadyExistException.class,
                () -> mainService.addUser(userInput)
        );

        Assertions.assertEquals("User already exists", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findByUserId(userInput.userId());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserEntity.class));
    }

    @Test
    @DisplayName("успешное удаление")
    void deleteUser_UserExists() {
        Long uId = 23L;
        UserEntity userEntity = new UserEntity(
                23L,
                "Fofa"
        );

        Mockito.when(userRepository.findById(uId))
                .thenReturn(Optional.of(userEntity));
        Mockito.doNothing().when(userRepository).deleteById(uId);

        mainService.deleteUser(uId);

        Mockito.verify(userRepository, Mockito.times(1)).findById(uId);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(uId);
    }

    @Test
    @DisplayName("юзера не существует - ошибка")
    void deleteUser_UserNotExists() {
        Long uId = 23L;

        Mockito.when(userRepository.findById(uId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> mainService.deleteUser(uId)
        );

        Assertions.assertEquals("User with id 23 not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(uId);
        Mockito.verify(userRepository, Mockito.never()).deleteById(uId);
    }

    @Test
    @DisplayName("успешное обновление юзера")
    void updateUser_UserExists() {
        Long uId = 23L;
        User userInput = new User(
                null,
                "Fofa"
        );
        UserEntity oldUserEntity = new UserEntity(
                23L,
                "Fufa"
        );
        UserEntity updatedUserEntity = new UserEntity(
                23L,
                "Fofa"
        );
        User userOutput = new User(
                23L,
                "Fofa"
        );

        Mockito.when(userRepository.findById(uId))
                .thenReturn(Optional.of(oldUserEntity));
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(updatedUserEntity);
        Mockito.when(userMapper.toDomain(Mockito.any(UserEntity.class)))
                .thenReturn(userOutput);

        User result = mainService.updateUser(uId, userInput);

        Assertions.assertEquals(userOutput, result);

        Mockito.verify(userRepository, Mockito.times(1)).findById(uId);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(UserEntity.class));
        Mockito.verify(userMapper, Mockito.times(1)).toDomain(Mockito.any(UserEntity.class));
    }

    @Test
    @DisplayName("юзер не найден")
    void updateUser_UserNotExists() {
        Long uId = 23L;
        User userInput = new User(
                null,
                "Fofa"
        );

        Mockito.when(userRepository.findById(uId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> mainService.updateUser(uId, userInput)
        );

        Assertions.assertEquals("User with id 23 not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(uId);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(UserEntity.class));
        Mockito.verify(userMapper, Mockito.never()).toDomain(Mockito.any(UserEntity.class));
    }

    @Test
    @DisplayName("успешное удаление локации")
    void deleteLocation_LocationExists() {
        Long lId = 23L;
        LocationEntity locationEntity = new LocationEntity(
                19L,
                "москва",
                "RU",
                "пасмурно",
                "04n",
                3.03,
                79L,
                "1.68",
                1772338896L,
                1772377358L,
                "18"
        );

        Mockito.when(locationRepository.findById(lId))
                        .thenReturn(Optional.of(locationEntity));
        Mockito.doNothing().when(locationRepository).deleteById(lId);

        mainService.deleteLocation(lId);

        Mockito.verify(locationRepository, Mockito.times(1)).findById(lId);
        Mockito.verify(locationRepository, Mockito.times(1)).deleteById(lId);
    }

    @Test
    @DisplayName("локации не сущесвует")
    void deleteLocation_LocationNotExists() {
        Long lId = 23L;

        Mockito.when(locationRepository.findById(lId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> mainService.deleteLocation(lId)
        );

        Assertions.assertEquals("Location with id 23 not found", exception.getMessage());

        Mockito.verify(locationRepository, Mockito.times(1)).findById(lId);
        Mockito.verify(locationRepository, Mockito.never()).deleteById(lId);
    }

    @Test
    @DisplayName("успешное получение локации, временный интервал совпадает с нынешним (то есть он не обновляется)")
    void getLocation_LocationExists_TimeIntervalEqualsNow() {
        String nameLocation = "москва";
        LocationEntity locationEntity = new LocationEntity(
                19L,
                "москва",
                "RU",
                "пасмурно",
                "04n",
                3.03,
                79L,
                "1.68",
                1772338896L,
                1772377358L,
                "18",
                LocalDate.of(20, 1, 1)
        );
        Location locationOut = new Location(
                19L,
                "москва",
                "RU",
                "пасмурно",
                "04n",
                3.03,
                79L,
                "1.68",
                1772338896L,
                1772377358L,
                "18"
        );

        Mockito.when(locationRepository.findByName(nameLocation))
                .thenReturn(Optional.of(locationEntity));
        Mockito.when(locationMapper.toDomain(Mockito.any()))
                .thenReturn(locationOut);
        Mockito.when(utils.calculateTheTimeInterval(Mockito.anyString()))
                .thenReturn("18");

        Location result = mainService.getLocation(nameLocation);

        Assertions.assertEquals(locationOut, result);

        Mockito.verify(locationRepository, Mockito.times(2)).findByName(nameLocation);
        Mockito.verify(utils, Mockito.times(2)).calculateTheTimeInterval(Mockito.anyString());
        Mockito.verify(locationMapper, Mockito.times(1)).toDomain(Mockito.any());
    }

    @Test
    @DisplayName("успешное получение локации, временный интервал не совпадает с нынешним (то есть он обновляется)")
    void getLocation_LocationExists_TimeIntervalNotEqualsNow() {
        String nameLocation = "москва";
        LocationEntity locationEntity = new LocationEntity(
                19L,
                "москва",
                "RU",
                "пасмурно",
                "04n",
                3.03,
                79L,
                "1.68",
                1772338896L,
                1772377358L,
                "18"
        );
        LocationEntity locationEntityGILOut = new LocationEntity(
                19L,
                "москва",
                "RU",
                "ясно",
                "04n",
                3.03,
                79L,
                "1.68",
                1772338896L,
                1772377358L,
                "00"
        );
        Location locationOut = new Location(
                19L,
                "москва",
                "RU",
                "ясно",
                "04n",
                3.03,
                79L,
                "1.68",
                1772338896L,
                1772377358L,
                "00"
        );

        Mockito.when(locationRepository.findByName(nameLocation))
                .thenReturn(Optional.of(locationEntity));
        Mockito.when(locationMapper.toDomain(Mockito.any(LocationEntity.class)))
                .thenReturn(locationOut);
        Mockito.when(utils.calculateTheTimeInterval(Mockito.anyString()))
                .thenReturn("00");
        Mockito.when(weatherService.getInfoLocation(Mockito.anyString()))
                .thenReturn(locationEntityGILOut);
        Mockito.when(locationRepository.save(Mockito.any(LocationEntity.class)))
                .thenReturn(locationEntityGILOut);

        Location result = mainService.getLocation(nameLocation);

        Assertions.assertEquals(locationOut, result);

        Mockito.verify(locationRepository, Mockito.times(2)).findByName(nameLocation);
        Mockito.verify(utils, Mockito.times(1)).calculateTheTimeInterval(Mockito.anyString());
        Mockito.verify(locationMapper, Mockito.times(1)).toDomain(Mockito.any(LocationEntity.class));
        Mockito.verify(weatherService, Mockito.times(1)).getInfoLocation(Mockito.anyString());
        Mockito.verify(locationRepository, Mockito.times(1)).save(Mockito.any(LocationEntity.class));
    }

    @Test
    @DisplayName("локация не найдена, ошибка")
    void getLocation_LocationNotExists() {
        String nameLocation = "моква";

        Mockito.when(locationRepository.findByName(nameLocation))
                .thenReturn(Optional.empty());
        Mockito.when(weatherService.getInfoLocation(Mockito.anyString()))
                .thenThrow(new NonExistentLocationNameException("Could not get info location"));

        NonExistentLocationNameException exception = Assertions.assertThrows(
                NonExistentLocationNameException.class,
                () -> mainService.getLocation(nameLocation)
        );

        Assertions.assertEquals("Could not get info location", exception.getMessage());

        Mockito.verify(locationRepository, Mockito.times(1)).findByName(nameLocation);
        Mockito.verify(utils, Mockito.never()).calculateTheTimeInterval(Mockito.anyString());
        Mockito.verify(locationMapper, Mockito.never()).toDomain(Mockito.any(LocationEntity.class));
        Mockito.verify(locationRepository, Mockito.never()).save(Mockito.any(LocationEntity.class));
    }

    @Test
    @DisplayName("юзер существует, локации успешно найдены")
    void findUserLocations_UserExists() {
        Long id = 23L;

        LocationEntity locationEntity1 = new LocationEntity(
                1L, "moscow", "RU", "облачно", "04d", 2.5, 78L, "3.6",
                1700000000L, 1700050000L, "12"
        );
        LocationEntity locationEntity2 = new LocationEntity(
                2L, "london", "GB", "пасмурно", "04n", 8.2, 85L, "6.1",
                1700001000L, 1700052000L, "15"
        );

        List<LocationEntity> locationEntities = List.of(locationEntity1, locationEntity2);

        UserEntity userEntity = new UserEntity(
                id,
                "Gogol",
                locationEntities
        );

        Location locationDto1 = new Location(
                1L, "moscow", "RU", "облачно", "04d", 2.5, 78L, "3.6",
                1700000000L, 1700050000L, "12"
        );
        Location locationDto2 = new Location(
                2L, "london", "GB", "пасмурно", "04n", 8.2, 85L, "6.1",
                1700001000L, 1700052000L, "15"
        );

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(locationMapper.toDomain(locationEntity1))
                .thenReturn(locationDto1);
        Mockito.when(locationMapper.toDomain(locationEntity2))
                .thenReturn(locationDto2);

        List<Location> result = mainService.findUserLocations(id);

        Assertions.assertEquals(locationDto1, result.get(0));
        Assertions.assertEquals(locationDto2, result.get(1));

        Mockito.verify(locationMapper, Mockito.times(1)).toDomain(locationEntity1);
        Mockito.verify(locationMapper, Mockito.times(1)).toDomain(locationEntity2);
    }

    @Test
    @DisplayName("юзера не существует, ошибка")
    void findUserLocations_UserNotExists() {
        Long id = 23L;

        Mockito.when(userRepository.findById(id))
                .thenThrow(new NoSuchElementException("User with id " + id + " not found"));

        NoSuchElementException exception = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> mainService.findUserLocations(id)
        );

        Assertions.assertEquals("User with id " + id + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("успешное удаление локации у юзера")
    void deleteLocationForUser_Successfully() {
        Long id = 23L;
        String name = "moscow";

        LocationEntity locationEntity1 = new LocationEntity(
                1L, "moscow", "RU", "облачно", "04d", 2.5, 78L, "3.6",
                1700000000L, 1700050000L, "12"
        );

        List<LocationEntity> locationEntities = new ArrayList<>(List.of(locationEntity1));

        UserEntity user = new UserEntity(
                23L,
                "Goga",
                locationEntities
        );

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        mainService.deleteLocationForUser(id, name);
        boolean result = user.getLocationEntities().isEmpty();

        Assertions.assertTrue(result);

        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("пользователь не найден")
    void deleteLocationForUser_UserNotFound_ThrowsException() {
        Long id = 23L;
        String name = "moscow";

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> mainService.deleteLocationForUser(id, name)
        );

        Assertions.assertEquals("User with id " + id + " not found", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("у пользователя нет такой локации")
    void deleteLocationForUser_LocationNotFound_ThrowsException() {
        Long id = 23L;
        String name = "paris";

        LocationEntity locationEntity1 = new LocationEntity(
                1L, "moscow", "RU", "облачно", "04d", 2.5, 78L, "3.6",
                1700000000L, 1700050000L, "12"
        );

        List<LocationEntity> locationEntities = new ArrayList<>();
        locationEntities.add(locationEntity1);

        UserEntity user = new UserEntity(23L, "Goga", locationEntities);

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        NoSuchElementException exception = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> mainService.deleteLocationForUser(id, name)
        );

        Assertions.assertEquals("User is not has location with name " + name, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

}

package com.example.weather_application;

import com.example.weather_application.controllers.WeatherController;
import com.example.weather_application.errors.LocationIsAlreadyIncludedException;
import com.example.weather_application.errors.UserAlreadyExistException;
import com.example.weather_application.location.Location;
import com.example.weather_application.location.LocationSearchFilter;
import com.example.weather_application.services.MainService;
import com.example.weather_application.services.WeatherService;
import com.example.weather_application.user.User;
import com.example.weather_application.user.UserSearchFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.NoSuchElementException;

@WebMvcTest(controllers = WeatherController.class)
public class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MainService mainService;
    @MockitoBean
    private WeatherService weatherService;

    @Test
    @DisplayName("POST /api/weather/users - успешное добавление")
    void postUserTest_UserNotExist_ProvideCreated201() throws Exception {
        User userInput = new User(
                null,
                "Globus27"
        );
        User userOutput = new User(
                23L,
                "Globus27"
        );

        Mockito.when(mainService.addUser(userInput)).thenReturn(userOutput);

        String requestBody = objectMapper.writeValueAsString(userInput);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(23L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("Globus27"));

        Mockito.verify(mainService, Mockito.times(1)).addUser(userInput);
    }

    @Test
    @DisplayName("POST /api/weather/users - юзер уже существует, ошибка")
    void postUserTest_UserExist_ProvideBadRequest400() throws Exception {
        User userInput = new User(
                null,
                "Globus27"
        );

        Mockito.when(mainService.addUser(Mockito.any(User.class))).thenThrow(UserAlreadyExistException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bad request"));

        Mockito.verify(mainService, Mockito.times(1)).addUser(userInput);
    }

    @Test
    @DisplayName("GET /api/weather/users? - успешное получение списка пользователей с пагинацией")
    void findUserByFilter_ShouldReturnUsersList() throws Exception {
        List<User> mockUsers = List.of(
                new User(1L, "Globus27"),
                new User(2L, "Ivan33"),
                new User(3L, "Petr44")
        );

        Mockito.when(mainService.searchAllUsersByFilter(Mockito.any(UserSearchFilter.class))).thenReturn(mockUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/users")
                        .param("id", "1")
                        .param("userId", "Globus27")
                        .param("pageNum", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value("Globus27"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userId").value("Ivan33"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].userId").value("Petr44"));

        Mockito.verify(mainService, Mockito.times(1)).searchAllUsersByFilter(Mockito.any(UserSearchFilter.class));
    }

    @Test
    @DisplayName("GET /api/weather/users? - без параметров (должны использоваться значения по умолчанию)")
    void findUserByFilter_WithoutParameters_ShouldUseDefaultValues() throws Exception {
        List<User> mockUsers = List.of(
                new User(1L, "Globus27")
        );

        Mockito.when(mainService.searchAllUsersByFilter(Mockito.any(UserSearchFilter.class))).thenReturn(mockUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        Mockito.verify(mainService, Mockito.times(1)).searchAllUsersByFilter(Mockito.any(UserSearchFilter.class));
    }

    @Test
    @DisplayName("GET /api/weather/locations? - успешное получение списка пользователей с пагинацией")
    void findLocationByFilter_ShouldReturnLocationsList() throws Exception {
        List<Location> mockLocations = List.of(
                new Location(
                        1L,
                        "london",
                        "GB",
                        "broken clouds",
                        "04d",
                        15.5,
                        72L,
                        "5.5",
                        1717890123L,
                        1717945678L,
                        "15"
                ),
                new Location(
                        2L,
                        "paris",
                        "FR",
                        "clear sky",
                        "01d",
                        22.3,
                        65L,
                        "3.2",
                        1717891234L,
                        1717946789L,
                        "15"
                ),
                new Location(
                        3L,
                        "berlin",
                        "DE",
                        "light rain",
                        "10d",
                        18.7,
                        80L,
                        "4.1",
                        1717892345L,
                        1717947890L,
                        "15"
                )
        );

        Mockito.when(mainService.searchAllLocationsByFilter(Mockito.any(LocationSearchFilter.class))).thenReturn(mockLocations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/locations")
                        .param("id", "1")
                        .param("name", "berlin")
                        .param("pageNum", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("london"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("paris"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("berlin"));

        Mockito.verify(mainService, Mockito.times(1)).searchAllLocationsByFilter(Mockito.any(LocationSearchFilter.class));
    }

    @Test
    @DisplayName("GET /api/weather/locations? - запрос без параметров")
    void findLocationByFilter_WithoutParameters_ShouldUseDefaultValues() throws Exception {
        List<Location> mockLocations = List.of(
                new Location(
                        1L,
                        "london",
                        "GB",
                        "broken clouds",
                        "04d",
                        15.5,
                        72L,
                        "5.5",
                        1717890123L,
                        1717945678L,
                        "15"
                )
        );

        Mockito.when(mainService.searchAllLocationsByFilter(Mockito.any(LocationSearchFilter.class))).thenReturn(mockLocations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        Mockito.verify(mainService, Mockito.times(1)).searchAllLocationsByFilter(Mockito.any(LocationSearchFilter.class));
    }

    @Test
    @DisplayName("DELETE /api/weather/users/{id} - успешное удаление")
    void deleteUserById_UserExists() throws Exception {
        Long id = 23L;

        Mockito.doNothing().when(mainService).deleteUser(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/users/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(mainService, Mockito.times(1)).deleteUser(id);
    }

    @Test
    @DisplayName("DELETE /api/weather/users/{id} - юзера не существует")
    void deleteUserById_UserNotExists() throws Exception {
        Long id = 23L;

        Mockito.when(mainService.deleteUser(id))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/users/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not found"));

        Mockito.verify(mainService, Mockito.times(1)).deleteUser(id);
    }

    @Test
    @DisplayName("DELETE /api/weather/locations/{id} - успешное удаление")
    void deleteLocationById_UserExists() throws Exception {
        Long id = 23L;

        Mockito.doNothing().when(mainService).deleteLocation(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/locations/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(mainService, Mockito.times(1)).deleteLocation(id);
    }

    @Test
    @DisplayName("DELETE /api/weather/locations/{id} - локации не существует")
    void deleteLocationById_UserNotExists() throws Exception {
        Long id = 23L;

        Mockito.when(mainService.deleteLocation(id))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/locations/" + id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not found"));

        Mockito.verify(mainService, Mockito.times(1)).deleteLocation(id);
    }

    @Test
    @DisplayName("PUT /api/weather/users/{userId}/locations - успешное добавление локации юзеру")
    void postLocationForUser_Expected200Ok() throws Exception {
        Long idU = 23L;
        Location locationInput = new Location(null, "париж", null, null, null, null, null, null, null, null, null);
        Location locationOutput = new Location(4L,
                "париж",
                "FR",
                "небольшая морось",
                "09d",
                -300.0,
                97L,
                "4.63",
                1769930464L,
                1769964429L,
                "18"
        );

        Mockito.when(weatherService.postLocationForUser(locationInput.name(), 23L))
                .thenReturn(locationOutput);

        String content = objectMapper.writeValueAsString(locationInput);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/weather/users/" + idU + "/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("париж"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value("FR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("небольшая морось"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.icon").value("09d"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature").value(-300.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.humidity").value(97L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.windSpeed").value("4.63"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunrise").value(1769930464L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunset").value(1769964429L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").value("18"));

        Mockito.verify(weatherService, Mockito.times(1)).postLocationForUser(locationInput.name(), idU);
    }

    @Test
    @DisplayName("PUT /api/weather/users/{user_id}/locations - локация уже есть у юзера")
    void postLocationForUser_LocationAlreadyExists_Expected500() throws Exception {
        Long idU = 23L;
        Location locationInput = new Location(null, "Париж", null, null, null, null, null, null, null, null, null);

        Mockito.when(weatherService.postLocationForUser(Mockito.anyString(), Mockito.eq(idU)))
                .thenThrow(new LocationIsAlreadyIncludedException("Location already exists"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/weather/users/" + idU + "/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationInput)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Something went wrong"));

        Mockito.verify(weatherService, Mockito.times(1)).postLocationForUser(Mockito.anyString(), Mockito.eq(idU));
    }

    @Test
    @DisplayName("PUT /api/weather/users/{user_id}/locations - некоректные данные")
    void postLocationForUser_IncorrectData_Expected400() throws Exception {
        Long idU = 23L;
        Location locationInput = new Location(null, "Приж", null, null, null, null, null, null, null, null, null);

        Mockito.when(weatherService.postLocationForUser(Mockito.anyString(), Mockito.eq(idU)))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/weather/users/" + idU + "/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Incorrect data, bad request"));

        Mockito.verify(weatherService, Mockito.times(1)).postLocationForUser(Mockito.anyString(), Mockito.eq(idU));
    }

    @Test
    @DisplayName("PUT /api/weather/users/{user_id}/locations - id не существует")
    void postLocationForUser_IdUserIsNotFound_Expected404() throws Exception {
        Long idU = 23L;
        Location locationInput = new Location(null, "Приж", null, null, null, null, null, null, null, null, null);

        Mockito.when(weatherService.postLocationForUser(Mockito.anyString(), Mockito.eq(idU)))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/weather/users/" + idU + "/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationInput)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not found"));

        Mockito.verify(weatherService, Mockito.times(1)).postLocationForUser(Mockito.anyString(), Mockito.eq(idU));
    }

    @Test
    @DisplayName("PUT /api/weather/users/{id_user} - успешное обновление данных юзеру")
    void updateUser_Expected200Ok() throws Exception {
        Long idU = 23L;
        User userInput = new User(
                null,
                "New Name"
        );
        User userOutput = new User(
                23L,
                "New Name"
        );

        Mockito.when(mainService.updateUser(Mockito.eq(idU), Mockito.any(User.class)))
                .thenReturn(userOutput);

        String response = objectMapper.writeValueAsString(userInput);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/weather/users/" + idU)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(response))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idU))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("New Name"));

        Mockito.verify(mainService, Mockito.times(1)).updateUser(Mockito.eq(idU), Mockito.any(User.class));
    }

    @Test
    @DisplayName("PUT /api/weather/users/{id_user} - юзер не найден")
    void updateUser_UserNotExists_Expected404() throws Exception {
        Long idU = 23L;
        User userInput = new User(
                null,
                "New Name"
        );

        Mockito.when(mainService.updateUser(Mockito.eq(idU), Mockito.any(User.class)))
                .thenThrow(new NoSuchElementException());

        String response = objectMapper.writeValueAsString(userInput);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/weather/users/" + idU)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(response))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not found"));

        Mockito.verify(mainService, Mockito.times(1)).updateUser(Mockito.eq(idU), Mockito.any(User.class));
    }

    @Test
    @DisplayName("GET /api/weather/info/locations? - успешное получение информации о локации")
    void getLocation_Expected200Ok() throws Exception {
        String locationName = "москва";
        Location locationOutput = new Location(
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

        Mockito.when(mainService.getLocation(locationName))
                .thenReturn(locationOutput);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/info/locations")
                        .param("name", locationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(19L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("москва"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value("RU"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("пасмурно"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.icon").value("04n"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature").value(3.03))
                .andExpect(MockMvcResultMatchers.jsonPath("$.humidity").value(79L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.windSpeed").value("1.68"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunrise").value(1772338896L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sunset").value(1772377358L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").value("18"));

        Mockito.verify(mainService, Mockito.times(1)).getLocation(locationName);
    }

    @Test
    @DisplayName("GET /api/weather/info/locations? - локация не найдена")
    void getLocation_LocationNotExists_Expected404() throws Exception {
        String locationName = "несуществующий_город";

        Mockito.when(mainService.getLocation(locationName))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/info/locations")
                        .param("name", locationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not found"));

        Mockito.verify(mainService, Mockito.times(1)).getLocation(locationName);
    }

    @Test
    @DisplayName("GET /api/weather/users/{id}/locations - успешное получение локаций для юзера")
    void getUserLocations_UserExists_Expected200Ok() throws Exception {
        Long id = 23L;
        List<Location> locationsOutput = List.of(
                new Location(
                        1L,
                        "moscow",
                        "RU",
                        "облачно",
                        "04d",
                        2.5,
                        78L,
                        "3.6",
                        1700000000L,
                        1700050000L,
                        "12"
                ),
                new Location(
                        2L,
                        "london",
                        "GB",
                        "пасмурно",
                        "04n",
                        8.2,
                        85L,
                        "6.1",
                        1700001000L,
                        1700052000L,
                        "15"
                )
        );

        Mockito.when(mainService.findUserLocations(Mockito.anyLong()))
                .thenReturn(locationsOutput);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/users/{id}/locations", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("moscow"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("london"));

        Mockito.verify(mainService, Mockito.times(1)).findUserLocations(id);
    }

    @Test
    @DisplayName("GET /api/weather/users/{id}/locations - юзера не существует")
    void getUserLocations_UserNotExists_Expected404() throws Exception {
        Long id = 23L;

        Mockito.when(mainService.findUserLocations(Mockito.anyLong()))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/users/{id}/locations", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(mainService, Mockito.times(1)).findUserLocations(Mockito.anyLong());
    }

    @Test
    @DisplayName("DELETE /api/weather/users/{id}/locations? - успешное удаление локации у юзера")
    void deleteUserLocations_UserExists_Expected200Ok() throws Exception {
        Long id = 23L;
        String locationName = "москва";

        Mockito.doReturn(null)
                .when(mainService).deleteLocationForUser(Mockito.eq(id), Mockito.eq(locationName));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/users/{id}/locations", id)
                        .param("name", locationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(mainService, Mockito.times(1)).deleteLocationForUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    @DisplayName("DELETE /api/weather/users/{id}/locations? - юзера не существует или локации нет у юзера")
    void deleteUserLocations_UserNotExists_Expected404() throws Exception {
        Long id = 23L;
        String locationName = "москва";

        Mockito.when(mainService.deleteLocationForUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/weather/users/{id}/locations", id)
                        .param("name", locationName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(mainService, Mockito.times(1)).deleteLocationForUser(Mockito.anyLong(), Mockito.anyString());
    }

}
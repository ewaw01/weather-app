package com.example.weather_application.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "cache_locations")
@Entity
public class LocationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "country")
    private String country;
    @Column(name = "description")
    private String description;
    @Column(name = "icon")
    private String icon;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "humidity")
    private Long humidity;
    @Column(name = "windSpeed")
    private String windSpeed;
    @Column(name = "sunrise")
    private Long sunrise;
    @Column(name = "sunset")
    private Long sunset;
    @Column(name = "time")
    private String time;
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
    @ManyToMany
    @JoinTable(
            name = "users_locations",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> usersEntities = new ArrayList<>();

    public LocationEntity() {
    }

    public LocationEntity(Long id, String name, String country, String description, String icon, Double temperature, Long humidity, String windSpeed, Long sunrise, Long sunset, String time) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.icon = icon;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.time = time;
    }

    public LocationEntity(Long id, String name, String country, String description, String icon, Double temperature, Long humidity, String windSpeed, Long sunrise, Long sunset, String time, LocalDate lastUpdated) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.icon = icon;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.time = time;
        this.lastUpdated = lastUpdated;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Long getHumidity() {
        return humidity;
    }

    public void setHumidity(Long humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<UserEntity> getUsersEntities() {
        return usersEntities;
    }

    public void setUsersEntities(List<UserEntity> usersEntities) {
        this.usersEntities = usersEntities;
    }

    public void addUser(UserEntity user) {
        if (!usersEntities.contains(user)) {
            usersEntities.add(user);
        }
    }

    public void removeUser(UserEntity user) {
        usersEntities.remove(user);
    }
}

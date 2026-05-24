package com.example.weather_application.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @ManyToMany
    @JoinTable(
            name = "users_locations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private List<LocationEntity> locationEntities = new ArrayList<>();

    public UserEntity(@NotNull String userId) {
        this.userId = userId;
    }

    public UserEntity() {
    }

    public UserEntity(Long id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public UserEntity(Long id, String userId, List<LocationEntity> locationEntities) {
        this.id = id;
        this.userId = userId;
        this.locationEntities = locationEntities;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<LocationEntity> getLocationEntities() {
        return locationEntities;
    }

    public void setLocationEntities(List<LocationEntity> locationEntities) {
        this.locationEntities = locationEntities;
    }

    public void addLocation(LocationEntity locationEntity) {
        if (!locationEntities.contains(locationEntity)) {
            locationEntities.add(locationEntity);
        }
    }

    public void removeLocation(LocationEntity locationEntity) {
        locationEntities.remove(locationEntity);
    }
}

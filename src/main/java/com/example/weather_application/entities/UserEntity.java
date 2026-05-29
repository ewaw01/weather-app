package com.example.weather_application.entities;

import com.example.weather_application.security.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "is_blocked")
    private Boolean isBlocked;
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
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

    public UserEntity(Long id, String email, String password, Role role, Boolean isBlocked, LocalDateTime createdAt, LocalDateTime updatedAt, String userId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBlocked = isBlocked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
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

}


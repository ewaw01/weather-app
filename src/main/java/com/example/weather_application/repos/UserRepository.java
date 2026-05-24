package com.example.weather_application.repos;

import com.example.weather_application.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE (:id IS NULL OR u.id = :id)
        AND (:user_id IS NULL OR u.userId = :user_id)
    """)
    Page<UserEntity> findAllByFilter(
            @Param("id") Long id,
            @Param("user_id") String userId,
            Pageable pageable
    );
}

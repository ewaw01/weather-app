package com.example.weather_application.repos;

import com.example.weather_application.entities.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    Optional<LocationEntity> findByName(String name);

    @Query("""
    SELECT l FROM LocationEntity l
        WHERE (l.id = :id OR :id IS NULL) AND
        (l.name = :name OR :name IS NULL) AND
        (l.country = :country OR :country IS NULL) AND
        (l.description = :description OR :description IS NULL) AND
        (l.icon = :icon OR :icon IS NULL) AND
        (l.temperature = :temperature OR :temperature IS NULL) AND
        (l.humidity = :humidity OR :humidity IS NULL) AND
        (l.windSpeed = :windSpeed OR :windSpeed IS NULL) AND
        (l.sunrise = :sunrise OR :sunrise IS NULL) AND
        (l.sunset = :sunset OR :sunset IS NULL) AND
        (l.time = :time OR :time IS NULL)
    """)
    Page<LocationEntity> findAllByFilter(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("country") String country,
            @Param("description") String description,
            @Param("icon") String icon,
            @Param("temperature") Double temperature,
            @Param("humidity") Long humidity,
            @Param("windSpeed") String windSpeed,
            @Param("sunrise") Long sunrise,
            @Param("sunset") Long sunset,
            @Param("time") String time,
            Pageable pageable
    );
}

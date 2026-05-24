package com.example.weather_application.mappers;

import com.example.weather_application.location.Location;
import com.example.weather_application.entities.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    public LocationEntity toEntity(
            Location location
    ) {
        return new LocationEntity(
                location.id(),
                location.name(),
                location.country(),
                location.description(),
                location.icon(),
                location.temperature(),
                location.humidity(),
                location.windSpeed(),
                location.sunrise(),
                location.sunset(),
                location.time()
        );
    }
    public Location toDomain(
            LocationEntity locationEntity
    ) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getCountry(),
                locationEntity.getDescription(),
                locationEntity.getIcon(),
                locationEntity.getTemperature(),
                locationEntity.getHumidity(),
                locationEntity.getWindSpeed(),
                locationEntity.getSunrise(),
                locationEntity.getSunset(),
                locationEntity.getTime()
        );
    }
}

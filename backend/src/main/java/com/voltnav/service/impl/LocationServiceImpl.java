package com.voltnav.service.impl;

import com.voltnav.dto.LocationDto;
import com.voltnav.entity.City;
import com.voltnav.entity.Location;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.repository.CityRepository;
import com.voltnav.repository.LocationRepository;
import com.voltnav.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final CityRepository cityRepository;

    public LocationServiceImpl(LocationRepository locationRepository, CityRepository cityRepository) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {
        City city = cityRepository.findById(locationDto.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + locationDto.getCityId()));

        Location location = new Location();
        location.setCity(city);
        location.setName(locationDto.getName().trim());
        location.setAddress(locationDto.getAddress().trim());
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setPostalCode(locationDto.getPostalCode());

        Location saved = locationRepository.save(location);
        return mapToDto(saved);
    }

    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDto> getLocationsByCityId(Long cityId) {
        return locationRepository.findByCityIdOrderByNameAsc(cityId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LocationDto getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return mapToDto(location);
    }

    @Override
    @Transactional
    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));

        if (locationDto.getCityId() != null && !location.getCity().getId().equals(locationDto.getCityId())) {
            City newCity = cityRepository.findById(locationDto.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + locationDto.getCityId()));
            location.setCity(newCity);
        }

        location.setName(locationDto.getName().trim());
        location.setAddress(locationDto.getAddress().trim());
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setPostalCode(locationDto.getPostalCode());

        Location updated = locationRepository.save(location);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        if (location.getStations() != null && !location.getStations().isEmpty()) {
            throw new BadRequestException("Cannot delete location with existing charging stations. Delete stations first.");
        }
        locationRepository.delete(location);
    }

    @Override
    public List<LocationDto> searchLocations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllLocations();
        }
        return locationRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private LocationDto mapToDto(Location location) {
        LocationDto dto = new LocationDto(
                location.getId(),
                location.getCity().getId(),
                location.getCity().getName(),
                location.getName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                location.getPostalCode()
        );
        dto.setCreatedAt(location.getCreatedAt());
        if (location.getStations() != null) {
            dto.setTotalStations(location.getStations().size());
        }
        return dto;
    }
}

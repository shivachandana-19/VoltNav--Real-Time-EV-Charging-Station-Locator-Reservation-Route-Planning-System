package com.voltnav.service.impl;

import com.voltnav.dto.CityDto;
import com.voltnav.entity.City;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceAlreadyExistsException;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.repository.CityRepository;
import com.voltnav.service.CityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public CityDto createCity(CityDto cityDto) {
        if (cityRepository.existsByNameIgnoreCase(cityDto.getName())) {
            throw new ResourceAlreadyExistsException("City already exists with name: " + cityDto.getName());
        }

        City city = new City();
        city.setName(cityDto.getName().trim());
        city.setState(cityDto.getState());
        city.setCountry(cityDto.getCountry() != null ? cityDto.getCountry() : "India");
        city.setDescription(cityDto.getDescription());
        city.setLatitude(cityDto.getLatitude());
        city.setLongitude(cityDto.getLongitude());
        city.setImageUrl(cityDto.getImageUrl());

        City saved = cityRepository.save(city);
        return mapToDto(saved);
    }

    @Override
    public List<CityDto> getAllCities() {
        return cityRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CityDto getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
        return mapToDto(city);
    }

    @Override
    @Transactional
    public CityDto updateCity(Long id, CityDto cityDto) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));

        if (!city.getName().equalsIgnoreCase(cityDto.getName()) &&
                cityRepository.existsByNameIgnoreCase(cityDto.getName())) {
            throw new ResourceAlreadyExistsException("Another city already exists with name: " + cityDto.getName());
        }

        city.setName(cityDto.getName().trim());
        city.setState(cityDto.getState());
        if (cityDto.getCountry() != null) {
            city.setCountry(cityDto.getCountry());
        }
        city.setDescription(cityDto.getDescription());
        city.setLatitude(cityDto.getLatitude());
        city.setLongitude(cityDto.getLongitude());
        city.setImageUrl(cityDto.getImageUrl());

        City updated = cityRepository.save(city);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
        if (city.getLocations() != null && !city.getLocations().isEmpty()) {
            throw new BadRequestException("Cannot delete city that has active locations. Delete locations first.");
        }
        cityRepository.delete(city);
    }

    @Override
    public List<CityDto> searchCities(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCities();
        }
        return cityRepository.findByNameContainingIgnoreCaseOrStateContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CityDto mapToDto(City city) {
        CityDto dto = new CityDto(
                city.getId(),
                city.getName(),
                city.getState(),
                city.getCountry(),
                city.getDescription(),
                city.getLatitude(),
                city.getLongitude(),
                city.getImageUrl()
        );
        dto.setCreatedAt(city.getCreatedAt());
        if (city.getLocations() != null) {
            dto.setTotalLocations(city.getLocations().size());
            int stationCount = city.getLocations().stream()
                    .mapToInt(l -> l.getStations() != null ? l.getStations().size() : 0)
                    .sum();
            dto.setTotalStations(stationCount);
        }
        return dto;
    }
}

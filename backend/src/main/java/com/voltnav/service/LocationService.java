package com.voltnav.service;

import com.voltnav.dto.LocationDto;
import java.util.List;

public interface LocationService {
    LocationDto createLocation(LocationDto locationDto);
    List<LocationDto> getAllLocations();
    List<LocationDto> getLocationsByCityId(Long cityId);
    LocationDto getLocationById(Long id);
    LocationDto updateLocation(Long id, LocationDto locationDto);
    void deleteLocation(Long id);
    List<LocationDto> searchLocations(String query);
}

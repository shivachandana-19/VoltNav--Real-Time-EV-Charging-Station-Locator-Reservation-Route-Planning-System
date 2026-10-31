package com.voltnav.service;

import com.voltnav.dto.CityDto;
import java.util.List;

public interface CityService {
    CityDto createCity(CityDto cityDto);
    List<CityDto> getAllCities();
    CityDto getCityById(Long id);
    CityDto updateCity(Long id, CityDto cityDto);
    void deleteCity(Long id);
    List<CityDto> searchCities(String query);
}

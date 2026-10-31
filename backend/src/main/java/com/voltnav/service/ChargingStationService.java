package com.voltnav.service;

import com.voltnav.dto.ChargingStationDto;
import com.voltnav.entity.StationStatus;
import java.util.List;

public interface ChargingStationService {
    ChargingStationDto createStation(ChargingStationDto stationDto);
    List<ChargingStationDto> getAllStations();
    List<ChargingStationDto> getStationsByCityId(Long cityId);
    List<ChargingStationDto> getStationsByLocationId(Long locationId);
    ChargingStationDto getStationById(Long id);
    ChargingStationDto updateStation(Long id, ChargingStationDto stationDto);
    void deleteStation(Long id);
    List<ChargingStationDto> searchStations(Long cityId, Long locationId, StationStatus status, String query);
}

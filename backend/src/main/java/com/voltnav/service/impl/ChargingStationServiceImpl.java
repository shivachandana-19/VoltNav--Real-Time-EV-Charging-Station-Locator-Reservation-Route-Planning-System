package com.voltnav.service.impl;

import com.voltnav.dto.ChargingStationDto;
import com.voltnav.entity.ChargingStation;
import com.voltnav.entity.Location;
import com.voltnav.entity.SlotStatus;
import com.voltnav.entity.StationStatus;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.repository.ChargingSlotRepository;
import com.voltnav.repository.ChargingStationRepository;
import com.voltnav.repository.LocationRepository;
import com.voltnav.service.ChargingStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChargingStationServiceImpl implements ChargingStationService {

    private final ChargingStationRepository stationRepository;
    private final LocationRepository locationRepository;
    private final ChargingSlotRepository slotRepository;

    public ChargingStationServiceImpl(ChargingStationRepository stationRepository,
                                      LocationRepository locationRepository,
                                      ChargingSlotRepository slotRepository) {
        this.stationRepository = stationRepository;
        this.locationRepository = locationRepository;
        this.slotRepository = slotRepository;
    }

    @Override
    @Transactional
    public ChargingStationDto createStation(ChargingStationDto stationDto) {
        Location location = locationRepository.findById(stationDto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + stationDto.getLocationId()));

        ChargingStation station = new ChargingStation();
        station.setLocation(location);
        station.setName(stationDto.getName().trim());
        station.setCompleteAddress(stationDto.getCompleteAddress().trim());
        station.setLatitude(stationDto.getLatitude());
        station.setLongitude(stationDto.getLongitude());
        station.setTotalChargers(stationDto.getTotalChargers() != null ? stationDto.getTotalChargers() : 4);
        station.setAvailableChargers(stationDto.getAvailableChargers() != null ? stationDto.getAvailableChargers() : station.getTotalChargers());
        station.setChargerType(stationDto.getChargerType().trim());
        station.setChargingSpeed(stationDto.getChargingSpeed().trim());
        station.setOperatingHours(stationDto.getOperatingHours() != null ? stationDto.getOperatingHours() : "24/7");
        station.setStatus(stationDto.getStatus() != null ? stationDto.getStatus() : StationStatus.ACTIVE);
        station.setPricingPerKwh(stationDto.getPricingPerKwh() != null ? stationDto.getPricingPerKwh() : 18.5);
        station.setContactNumber(stationDto.getContactNumber());

        ChargingStation saved = stationRepository.save(station);
        return mapToDto(saved);
    }

    @Override
    public List<ChargingStationDto> getAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChargingStationDto> getStationsByCityId(Long cityId) {
        return stationRepository.findByLocationCityId(cityId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChargingStationDto> getStationsByLocationId(Long locationId) {
        return stationRepository.findByLocationId(locationId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStationDto getStationById(Long id) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging station not found with id: " + id));
        return mapToDto(station);
    }

    @Override
    @Transactional
    public ChargingStationDto updateStation(Long id, ChargingStationDto stationDto) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging station not found with id: " + id));

        if (stationDto.getLocationId() != null && !station.getLocation().getId().equals(stationDto.getLocationId())) {
            Location newLocation = locationRepository.findById(stationDto.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + stationDto.getLocationId()));
            station.setLocation(newLocation);
        }

        station.setName(stationDto.getName().trim());
        station.setCompleteAddress(stationDto.getCompleteAddress().trim());
        station.setLatitude(stationDto.getLatitude());
        station.setLongitude(stationDto.getLongitude());
        station.setTotalChargers(stationDto.getTotalChargers());
        station.setAvailableChargers(stationDto.getAvailableChargers());
        station.setChargerType(stationDto.getChargerType().trim());
        station.setChargingSpeed(stationDto.getChargingSpeed().trim());
        station.setOperatingHours(stationDto.getOperatingHours());
        if (stationDto.getStatus() != null) {
            station.setStatus(stationDto.getStatus());
        }
        station.setPricingPerKwh(stationDto.getPricingPerKwh());
        station.setContactNumber(stationDto.getContactNumber());

        ChargingStation updated = stationRepository.save(station);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteStation(Long id) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging station not found with id: " + id));
        if (station.getSlots() != null && !station.getSlots().isEmpty()) {
            boolean hasBookings = station.getSlots().stream().anyMatch(s -> s.getStatus() == SlotStatus.BOOKED);
            if (hasBookings) {
                throw new BadRequestException("Cannot delete station with active booked slots.");
            }
        }
        stationRepository.delete(station);
    }

    @Override
    public List<ChargingStationDto> searchStations(Long cityId, Long locationId, StationStatus status, String query) {
        return stationRepository.searchStations(cityId, locationId, status, query)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ChargingStationDto mapToDto(ChargingStation station) {
        ChargingStationDto dto = new ChargingStationDto();
        dto.setId(station.getId());
        dto.setLocationId(station.getLocation().getId());
        dto.setLocationName(station.getLocation().getName());
        dto.setCityId(station.getLocation().getCity().getId());
        dto.setCityName(station.getLocation().getCity().getName());
        dto.setName(station.getName());
        dto.setCompleteAddress(station.getCompleteAddress());
        dto.setLatitude(station.getLatitude());
        dto.setLongitude(station.getLongitude());
        dto.setTotalChargers(station.getTotalChargers());
        dto.setAvailableChargers(station.getAvailableChargers());
        dto.setChargerType(station.getChargerType());
        dto.setChargingSpeed(station.getChargingSpeed());
        dto.setOperatingHours(station.getOperatingHours());
        dto.setStatus(station.getStatus());
        dto.setPricingPerKwh(station.getPricingPerKwh());
        dto.setContactNumber(station.getContactNumber());
        dto.setCreatedAt(station.getCreatedAt());

        if (station.getSlots() != null) {
            dto.setTotalSlots(station.getSlots().size());
            long availableCount = station.getSlots().stream()
                    .filter(s -> s.getStatus() == SlotStatus.AVAILABLE)
                    .count();
            dto.setAvailableSlots((int) availableCount);
        }
        return dto;
    }
}

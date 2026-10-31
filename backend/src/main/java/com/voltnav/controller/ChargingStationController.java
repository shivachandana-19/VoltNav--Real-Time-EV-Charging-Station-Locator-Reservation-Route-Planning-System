package com.voltnav.controller;

import com.voltnav.dto.ChargingStationDto;
import com.voltnav.entity.StationStatus;
import com.voltnav.service.ChargingStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@Tag(name = "Charging Stations", description = "Discover, inspect, filter, and manage EV Charging Stations")
public class ChargingStationController {

    private final ChargingStationService stationService;

    public ChargingStationController(ChargingStationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    @Operation(summary = "Get all charging stations")
    public ResponseEntity<List<ChargingStationDto>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get charging station by ID")
    public ResponseEntity<ChargingStationDto> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "Get charging stations in a specific city")
    public ResponseEntity<List<ChargingStationDto>> getStationsByCityId(@PathVariable Long cityId) {
        return ResponseEntity.ok(stationService.getStationsByCityId(cityId));
    }

    @GetMapping("/location/{locationId}")
    @Operation(summary = "Get charging stations in a specific location")
    public ResponseEntity<List<ChargingStationDto>> getStationsByLocationId(@PathVariable Long locationId) {
        return ResponseEntity.ok(stationService.getStationsByLocationId(locationId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter charging stations by city, location, status, and query keyword")
    public ResponseEntity<List<ChargingStationDto>> searchStations(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) StationStatus status,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(stationService.searchStations(cityId, locationId, status, query));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new charging station (Admin only)")
    public ResponseEntity<ChargingStationDto> createStation(@Valid @RequestBody ChargingStationDto stationDto) {
        ChargingStationDto created = stationService.createStation(stationDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update charging station details (Admin only)")
    public ResponseEntity<ChargingStationDto> updateStation(
            @PathVariable Long id, @Valid @RequestBody ChargingStationDto stationDto) {
        return ResponseEntity.ok(stationService.updateStation(id, stationDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete charging station (Admin only)")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}

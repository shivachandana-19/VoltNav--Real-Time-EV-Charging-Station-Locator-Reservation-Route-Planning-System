package com.voltnav.controller;

import com.voltnav.dto.CityDto;
import com.voltnav.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@Tag(name = "Cities", description = "Manage and view operating cities for EV charging stations")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "Get all available cities")
    public ResponseEntity<List<CityDto>> getAllCities() {
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get city by ID")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.getCityById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search cities by name or state")
    public ResponseEntity<List<CityDto>> searchCities(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(cityService.searchCities(query));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new city (Admin only)")
    public ResponseEntity<CityDto> createCity(@Valid @RequestBody CityDto cityDto) {
        CityDto created = cityService.createCity(cityDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update city details (Admin only)")
    public ResponseEntity<CityDto> updateCity(@PathVariable Long id, @Valid @RequestBody CityDto cityDto) {
        return ResponseEntity.ok(cityService.updateCity(id, cityDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete city (Admin only)")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}

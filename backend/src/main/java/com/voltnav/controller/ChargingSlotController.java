package com.voltnav.controller;

import com.voltnav.dto.BulkSlotCreateRequest;
import com.voltnav.dto.ChargingSlotDto;
import com.voltnav.entity.SlotStatus;
import com.voltnav.service.ChargingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
@Tag(name = "Charging Slots", description = "Query and manage time slots for EV charging stations")
public class ChargingSlotController {

    private final ChargingSlotService slotService;

    public ChargingSlotController(ChargingSlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping("/station/{stationId}")
    @Operation(summary = "Get all slots for a station on a specific date")
    public ResponseEntity<List<ChargingSlotDto>> getSlotsByStationAndDate(
            @PathVariable Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotService.getSlotsByStationAndDate(stationId, date));
    }

    @GetMapping("/station/{stationId}/available")
    @Operation(summary = "Get only available slots for a station on a specific date")
    public ResponseEntity<List<ChargingSlotDto>> getAvailableSlots(
            @PathVariable Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotService.getAvailableSlots(stationId, date));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an individual slot (Admin only)")
    public ResponseEntity<ChargingSlotDto> createSlot(@Valid @RequestBody ChargingSlotDto slotDto) {
        ChargingSlotDto created = slotService.createSlot(slotDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk generate slots for a charging station (Admin only)")
    public ResponseEntity<List<ChargingSlotDto>> generateBulkSlots(@Valid @RequestBody BulkSlotCreateRequest request) {
        List<ChargingSlotDto> createdSlots = slotService.generateBulkSlots(request);
        return new ResponseEntity<>(createdSlots, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update slot timings or price (Admin only)")
    public ResponseEntity<ChargingSlotDto> updateSlot(
            @PathVariable Long id, @Valid @RequestBody ChargingSlotDto slotDto) {
        return ResponseEntity.ok(slotService.updateSlot(id, slotDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change slot availability status (Admin only)")
    public ResponseEntity<ChargingSlotDto> updateSlotStatus(
            @PathVariable Long id, @RequestParam SlotStatus status) {
        return ResponseEntity.ok(slotService.updateSlotStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete slot (Admin only)")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
}

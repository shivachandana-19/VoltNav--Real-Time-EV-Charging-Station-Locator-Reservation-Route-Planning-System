package com.voltnav.service;

import com.voltnav.dto.BulkSlotCreateRequest;
import com.voltnav.dto.ChargingSlotDto;
import com.voltnav.entity.SlotStatus;
import java.time.LocalDate;
import java.util.List;

public interface ChargingSlotService {
    ChargingSlotDto createSlot(ChargingSlotDto slotDto);
    List<ChargingSlotDto> generateBulkSlots(BulkSlotCreateRequest request);
    List<ChargingSlotDto> getSlotsByStationAndDate(Long stationId, LocalDate date);
    List<ChargingSlotDto> getAvailableSlots(Long stationId, LocalDate date);
    ChargingSlotDto updateSlot(Long id, ChargingSlotDto slotDto);
    void deleteSlot(Long id);
    ChargingSlotDto updateSlotStatus(Long id, SlotStatus status);
}

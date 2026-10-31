package com.voltnav.service.impl;

import com.voltnav.dto.BulkSlotCreateRequest;
import com.voltnav.dto.ChargingSlotDto;
import com.voltnav.entity.ChargingSlot;
import com.voltnav.entity.ChargingStation;
import com.voltnav.entity.SlotStatus;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.repository.ChargingSlotRepository;
import com.voltnav.repository.ChargingStationRepository;
import com.voltnav.service.ChargingSlotService;
import com.voltnav.util.AppConstants;
import com.voltnav.websocket.SlotUpdateMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChargingSlotServiceImpl implements ChargingSlotService {

    private final ChargingSlotRepository slotRepository;
    private final ChargingStationRepository stationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChargingSlotServiceImpl(ChargingSlotRepository slotRepository,
                                  ChargingStationRepository stationRepository,
                                  SimpMessagingTemplate messagingTemplate) {
        this.slotRepository = slotRepository;
        this.stationRepository = stationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public ChargingSlotDto createSlot(ChargingSlotDto slotDto) {
        ChargingStation station = stationRepository.findById(slotDto.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + slotDto.getStationId()));

        if (slotDto.getEndTime().isBefore(slotDto.getStartTime()) || slotDto.getEndTime().equals(slotDto.getStartTime())) {
            throw new BadRequestException("Slot end time must be after start time");
        }

        ChargingSlot slot = new ChargingSlot();
        slot.setStation(station);
        slot.setSlotNumber(slotDto.getSlotNumber() != null ? slotDto.getSlotNumber() : "Slot " + (station.getSlots().size() + 1));
        slot.setSlotDate(slotDto.getSlotDate());
        slot.setStartTime(slotDto.getStartTime());
        slot.setEndTime(slotDto.getEndTime());
        slot.setStatus(slotDto.getStatus() != null ? slotDto.getStatus() : SlotStatus.AVAILABLE);
        slot.setPrice(slotDto.getPrice() != null ? slotDto.getPrice() : 250.0);

        ChargingSlot saved = slotRepository.save(slot);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public List<ChargingSlotDto> generateBulkSlots(BulkSlotCreateRequest request) {
        ChargingStation station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + request.getStationId()));

        List<ChargingSlot> createdSlots = new ArrayList<>();
        LocalTime current = request.getStartTime();
        int duration = request.getDurationMinutes() != null ? request.getDurationMinutes() : 60;
        int chargersCount = request.getNumberOfSlotsPerInterval() != null ? request.getNumberOfSlotsPerInterval() : 2;

        while (current.plusMinutes(duration).isBefore(request.getEndTime()) || current.plusMinutes(duration).equals(request.getEndTime())) {
            LocalTime slotEnd = current.plusMinutes(duration);
            for (int i = 1; i <= chargersCount; i++) {
                ChargingSlot slot = new ChargingSlot();
                slot.setStation(station);
                slot.setSlotNumber("Charger #" + i + " (" + station.getChargerType().split(",")[0].trim() + ")");
                slot.setSlotDate(request.getSlotDate());
                slot.setStartTime(current);
                slot.setEndTime(slotEnd);
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setPrice(request.getPrice() != null ? request.getPrice() : 250.0);
                createdSlots.add(slot);
            }
            current = slotEnd;
        }

        List<ChargingSlot> saved = slotRepository.saveAll(createdSlots);
        return saved.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<ChargingSlotDto> getSlotsByStationAndDate(Long stationId, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        return slotRepository.findByStationIdAndSlotDateOrderByStartTimeAsc(stationId, queryDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChargingSlotDto> getAvailableSlots(Long stationId, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        return slotRepository.findByStationIdAndSlotDateAndStatusOrderByStartTimeAsc(stationId, queryDate, SlotStatus.AVAILABLE)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChargingSlotDto updateSlot(Long id, ChargingSlotDto slotDto) {
        ChargingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + id));

        if (slotDto.getSlotNumber() != null) slot.setSlotNumber(slotDto.getSlotNumber());
        if (slotDto.getSlotDate() != null) slot.setSlotDate(slotDto.getSlotDate());
        if (slotDto.getStartTime() != null) slot.setStartTime(slotDto.getStartTime());
        if (slotDto.getEndTime() != null) slot.setEndTime(slotDto.getEndTime());
        if (slotDto.getStatus() != null) slot.setStatus(slotDto.getStatus());
        if (slotDto.getPrice() != null) slot.setPrice(slotDto.getPrice());

        ChargingSlot updated = slotRepository.save(slot);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) {
        ChargingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + id));
        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new BadRequestException("Cannot delete a booked charging slot.");
        }
        slotRepository.delete(slot);
    }

    @Override
    @Transactional
    public ChargingSlotDto updateSlotStatus(Long id, SlotStatus status) {
        ChargingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + id));

        slot.setStatus(status);
        ChargingSlot updated = slotRepository.save(slot);

        // Broadcast slot change via WebSocket
        try {
            long availableCount = slotRepository.countByStationIdAndStatus(slot.getStation().getId(), SlotStatus.AVAILABLE);
            SlotUpdateMessage msg = new SlotUpdateMessage(
                    slot.getStation().getId(),
                    slot.getId(),
                    slot.getSlotNumber(),
                    status.name(),
                    (int) availableCount,
                    slot.getStation().getTotalChargers()
            );
            messagingTemplate.convertAndSend(AppConstants.WS_TOPIC_SLOTS, msg);
        } catch (Exception ignored) {
        }

        return mapToDto(updated);
    }

    private ChargingSlotDto mapToDto(ChargingSlot slot) {
        ChargingSlotDto dto = new ChargingSlotDto();
        dto.setId(slot.getId());
        dto.setStationId(slot.getStation().getId());
        dto.setStationName(slot.getStation().getName());
        dto.setSlotNumber(slot.getSlotNumber());
        dto.setSlotDate(slot.getSlotDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        dto.setPrice(slot.getPrice());
        dto.setCreatedAt(slot.getCreatedAt());
        return dto;
    }
}

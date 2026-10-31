package com.voltnav.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class BulkSlotCreateRequest {

    @NotNull(message = "Station ID is required")
    private Long stationId;

    @NotNull(message = "Date is required")
    private LocalDate slotDate;

    private LocalTime startTime = LocalTime.of(8, 0);
    private LocalTime endTime = LocalTime.of(22, 0);
    private Integer durationMinutes = 60;
    private Integer numberOfSlotsPerInterval = 2;
    private Double price = 250.0;

    public BulkSlotCreateRequest() {
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getNumberOfSlotsPerInterval() {
        return numberOfSlotsPerInterval;
    }

    public void setNumberOfSlotsPerInterval(Integer numberOfSlotsPerInterval) {
        this.numberOfSlotsPerInterval = numberOfSlotsPerInterval;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

package com.voltnav.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequest {

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    private Long stationId;
    private LocalDate bookingDate;
    private String vehicleNumber;
    private String remarks;

    public BookingRequest() {
    }

    public BookingRequest(Long slotId, Long stationId, LocalDate bookingDate, String vehicleNumber, String remarks) {
        this.slotId = slotId;
        this.stationId = stationId;
        this.bookingDate = bookingDate;
        this.vehicleNumber = vehicleNumber;
        this.remarks = remarks;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

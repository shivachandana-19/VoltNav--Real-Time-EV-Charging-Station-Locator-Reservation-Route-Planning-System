package com.voltnav.websocket;

import java.time.LocalDateTime;

public class SlotUpdateMessage {

    private Long stationId;
    private Long slotId;
    private String slotNumber;
    private String status;
    private Integer availableChargers;
    private Integer totalChargers;
    private LocalDateTime timestamp;

    public SlotUpdateMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public SlotUpdateMessage(Long stationId, Long slotId, String slotNumber, String status,
                             Integer availableChargers, Integer totalChargers) {
        this.stationId = stationId;
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.status = status;
        this.availableChargers = availableChargers;
        this.totalChargers = totalChargers;
        this.timestamp = LocalDateTime.now();
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAvailableChargers() {
        return availableChargers;
    }

    public void setAvailableChargers(Integer availableChargers) {
        this.availableChargers = availableChargers;
    }

    public Integer getTotalChargers() {
        return totalChargers;
    }

    public void setTotalChargers(Integer totalChargers) {
        this.totalChargers = totalChargers;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

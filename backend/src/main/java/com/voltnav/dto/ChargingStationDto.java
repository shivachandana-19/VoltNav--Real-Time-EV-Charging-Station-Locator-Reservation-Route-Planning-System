package com.voltnav.dto;

import com.voltnav.entity.StationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ChargingStationDto {

    private Long id;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    private String locationName;
    private Long cityId;
    private String cityName;

    @NotBlank(message = "Station name is required")
    private String name;

    @NotBlank(message = "Complete address is required")
    private String completeAddress;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private Integer totalChargers = 4;
    private Integer availableChargers = 4;

    @NotBlank(message = "Charger type is required")
    private String chargerType;

    @NotBlank(message = "Charging speed is required")
    private String chargingSpeed;

    private String operatingHours = "24/7";
    private StationStatus status = StationStatus.ACTIVE;
    private Double pricingPerKwh = 18.5;
    private String contactNumber;
    private int totalSlots;
    private int availableSlots;
    private LocalDateTime createdAt;

    public ChargingStationDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        this.completeAddress = completeAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getTotalChargers() {
        return totalChargers;
    }

    public void setTotalChargers(Integer totalChargers) {
        this.totalChargers = totalChargers;
    }

    public Integer getAvailableChargers() {
        return availableChargers;
    }

    public void setAvailableChargers(Integer availableChargers) {
        this.availableChargers = availableChargers;
    }

    public String getChargerType() {
        return chargerType;
    }

    public void setChargerType(String chargerType) {
        this.chargerType = chargerType;
    }

    public String getChargingSpeed() {
        return chargingSpeed;
    }

    public void setChargingSpeed(String chargingSpeed) {
        this.chargingSpeed = chargingSpeed;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public Double getPricingPerKwh() {
        return pricingPerKwh;
    }

    public void setPricingPerKwh(Double pricingPerKwh) {
        this.pricingPerKwh = pricingPerKwh;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

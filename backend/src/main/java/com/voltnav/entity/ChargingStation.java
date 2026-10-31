package com.voltnav.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "charging_stations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnoreProperties("stations")
    private Location location;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "complete_address", nullable = false, length = 255)
    private String completeAddress;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "total_chargers", nullable = false)
    private Integer totalChargers = 4;

    @Column(name = "available_chargers", nullable = false)
    private Integer availableChargers = 4;

    @Column(name = "charger_type", nullable = false, length = 100)
    private String chargerType; // e.g. "CCS2, Type 2, CHAdeMO"

    @Column(name = "charging_speed", nullable = false, length = 50)
    private String chargingSpeed; // e.g. "50 kW DC Fast", "150 kW Ultra Fast"

    @Column(name = "operating_hours", length = 50)
    private String operatingHours = "24/7";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StationStatus status = StationStatus.ACTIVE;

    @Column(name = "pricing_per_kwh")
    private Double pricingPerKwh = 18.50;

    @Column(name = "contact_number", length = 30)
    private String contactNumber;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("station")
    private List<ChargingSlot> slots = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChargingStation() {
    }

    public ChargingStation(Location location, String name, String completeAddress, Double latitude, Double longitude,
                           Integer totalChargers, Integer availableChargers, String chargerType, String chargingSpeed,
                           String operatingHours, StationStatus status, Double pricingPerKwh, String contactNumber) {
        this.location = location;
        this.name = name;
        this.completeAddress = completeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalChargers = totalChargers;
        this.availableChargers = availableChargers;
        this.chargerType = chargerType;
        this.chargingSpeed = chargingSpeed;
        this.operatingHours = operatingHours;
        this.status = status != null ? status : StationStatus.ACTIVE;
        this.pricingPerKwh = pricingPerKwh;
        this.contactNumber = contactNumber;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public List<ChargingSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<ChargingSlot> slots) {
        this.slots = slots;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.voltnav.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CityDto {

    private Long id;

    @NotBlank(message = "City name is required")
    private String name;

    private String state;
    private String country = "India";
    private String description;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private int totalLocations;
    private int totalStations;
    private LocalDateTime createdAt;

    public CityDto() {
    }

    public CityDto(Long id, String name, String state, String country, String description,
                   Double latitude, Double longitude, String imageUrl) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.country = country;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTotalLocations() {
        return totalLocations;
    }

    public void setTotalLocations(int totalLocations) {
        this.totalLocations = totalLocations;
    }

    public int getTotalStations() {
        return totalStations;
    }

    public void setTotalStations(int totalStations) {
        this.totalStations = totalStations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

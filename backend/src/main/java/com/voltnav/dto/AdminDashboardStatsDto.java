package com.voltnav.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardStatsDto {

    private long totalUsers;
    private long totalCities;
    private long totalLocations;
    private long totalStations;
    private long totalSlots;
    private long availableSlots;
    private long bookedSlots;
    private long totalReservations;
    private List<BookingResponse> recentBookings = new ArrayList<>();

    // Chart analytics
    private List<String> dateLabels = new ArrayList<>();
    private List<Long> dateCounts = new ArrayList<>();

    private List<String> cityLabels = new ArrayList<>();
    private List<Long> cityCounts = new ArrayList<>();

    private List<String> stationLabels = new ArrayList<>();
    private List<Long> stationCounts = new ArrayList<>();

    public AdminDashboardStatsDto() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalCities() {
        return totalCities;
    }

    public void setTotalCities(long totalCities) {
        this.totalCities = totalCities;
    }

    public long getTotalLocations() {
        return totalLocations;
    }

    public void setTotalLocations(long totalLocations) {
        this.totalLocations = totalLocations;
    }

    public long getTotalStations() {
        return totalStations;
    }

    public void setTotalStations(long totalStations) {
        this.totalStations = totalStations;
    }

    public long getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(long totalSlots) {
        this.totalSlots = totalSlots;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public long getBookedSlots() {
        return bookedSlots;
    }

    public void setBookedSlots(long bookedSlots) {
        this.bookedSlots = bookedSlots;
    }

    public long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public List<BookingResponse> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<BookingResponse> recentBookings) {
        this.recentBookings = recentBookings;
    }

    public List<String> getDateLabels() {
        return dateLabels;
    }

    public void setDateLabels(List<String> dateLabels) {
        this.dateLabels = dateLabels;
    }

    public List<Long> getDateCounts() {
        return dateCounts;
    }

    public void setDateCounts(List<Long> dateCounts) {
        this.dateCounts = dateCounts;
    }

    public List<String> getCityLabels() {
        return cityLabels;
    }

    public void setCityLabels(List<String> cityLabels) {
        this.cityLabels = cityLabels;
    }

    public List<Long> getCityCounts() {
        return cityCounts;
    }

    public void setCityCounts(List<Long> cityCounts) {
        this.cityCounts = cityCounts;
    }

    public List<String> getStationLabels() {
        return stationLabels;
    }

    public void setStationLabels(List<String> stationLabels) {
        this.stationLabels = stationLabels;
    }

    public List<Long> getStationCounts() {
        return stationCounts;
    }

    public void setStationCounts(List<Long> stationCounts) {
        this.stationCounts = stationCounts;
    }
}

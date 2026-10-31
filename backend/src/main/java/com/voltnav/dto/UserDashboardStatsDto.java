package com.voltnav.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardStatsDto {

    private long totalBookings;
    private long upcomingBookings;
    private long completedBookings;
    private long cancelledBookings;
    private List<BookingResponse> recentBookings = new ArrayList<>();
    private List<ChargingStationDto> nearbyStations = new ArrayList<>();

    public UserDashboardStatsDto() {
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(long upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public long getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(long completedBookings) {
        this.completedBookings = completedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public List<BookingResponse> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<BookingResponse> recentBookings) {
        this.recentBookings = recentBookings;
    }

    public List<ChargingStationDto> getNearbyStations() {
        return nearbyStations;
    }

    public void setNearbyStations(List<ChargingStationDto> nearbyStations) {
        this.nearbyStations = nearbyStations;
    }
}

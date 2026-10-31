package com.voltnav.service.impl;

import com.voltnav.dto.AdminDashboardStatsDto;
import com.voltnav.dto.BookingResponse;
import com.voltnav.dto.ChargingStationDto;
import com.voltnav.dto.UserDashboardStatsDto;
import com.voltnav.entity.Booking;
import com.voltnav.entity.BookingStatus;
import com.voltnav.entity.SlotStatus;
import com.voltnav.entity.User;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.repository.*;
import com.voltnav.service.BookingService;
import com.voltnav.service.ChargingStationService;
import com.voltnav.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final ChargingStationRepository stationRepository;
    private final ChargingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ChargingStationService stationService;

    public DashboardServiceImpl(UserRepository userRepository,
                                CityRepository cityRepository,
                                LocationRepository locationRepository,
                                ChargingStationRepository stationRepository,
                                ChargingSlotRepository slotRepository,
                                BookingRepository bookingRepository,
                                BookingService bookingService,
                                ChargingStationService stationService) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.stationRepository = stationRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.stationService = stationService;
    }

    @Override
    public AdminDashboardStatsDto getAdminDashboardStats() {
        AdminDashboardStatsDto dto = new AdminDashboardStatsDto();
        dto.setTotalUsers(userRepository.count());
        dto.setTotalCities(cityRepository.count());
        dto.setTotalLocations(locationRepository.count());
        dto.setTotalStations(stationRepository.count());
        dto.setTotalSlots(slotRepository.count());
        dto.setAvailableSlots(slotRepository.countByStatus(SlotStatus.AVAILABLE));
        dto.setBookedSlots(slotRepository.countByStatus(SlotStatus.BOOKED));
        dto.setTotalReservations(bookingRepository.count());

        // Recent bookings (top 8)
        List<BookingResponse> recent = bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(8)
                .map(b -> bookingService.getBookingById(b.getId()))
                .collect(Collectors.toList());
        dto.setRecentBookings(recent);

        // Date-wise analytics
        List<Object[]> dateData = bookingRepository.countBookingsGroupedByDate();
        for (Object[] row : dateData) {
            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];
            dto.getDateLabels().add(date.toString());
            dto.getDateCounts().add(count);
        }

        // City-wise analytics
        List<Object[]> cityData = bookingRepository.countBookingsGroupedByCity();
        for (Object[] row : cityData) {
            String city = (String) row[0];
            Long count = (Long) row[1];
            dto.getCityLabels().add(city != null ? city : "Unknown");
            dto.getCityCounts().add(count);
        }

        // Station-wise analytics (top 5)
        List<Object[]> stationData = bookingRepository.countBookingsGroupedByStation();
        for (Object[] row : stationData) {
            if (dto.getStationLabels().size() >= 5) break;
            String station = (String) row[0];
            Long count = (Long) row[1];
            dto.getStationLabels().add(station);
            dto.getStationCounts().add(count);
        }

        return dto;
    }

    @Override
    public UserDashboardStatsDto getUserDashboardStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        UserDashboardStatsDto dto = new UserDashboardStatsDto();
        List<Booking> userBookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        dto.setTotalBookings(userBookings.size());
        dto.setUpcomingBookings(userBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count());
        dto.setCompletedBookings(userBookings.stream().filter(b -> b.getStatus() == BookingStatus.CHECKED_IN || b.getStatus() == BookingStatus.COMPLETED).count());
        dto.setCancelledBookings(userBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count());

        List<BookingResponse> recent = userBookings.stream()
                .limit(5)
                .map(b -> bookingService.getBookingById(b.getId()))
                .collect(Collectors.toList());
        dto.setRecentBookings(recent);

        // Fetch top nearby stations (limit 6)
        List<ChargingStationDto> stations = stationService.getAllStations()
                .stream()
                .limit(6)
                .collect(Collectors.toList());
        dto.setNearbyStations(stations);

        return dto;
    }
}

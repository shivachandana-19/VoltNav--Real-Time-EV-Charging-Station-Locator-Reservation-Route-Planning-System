package com.voltnav.service;

import com.voltnav.dto.BookingRequest;
import com.voltnav.dto.BookingResponse;
import com.voltnav.entity.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String userEmail);
    List<BookingResponse> getUserBookings(String userEmail);
    List<BookingResponse> getAllBookings(BookingStatus status, Long cityId, Long stationId, LocalDate bookingDate, String query);
    BookingResponse getBookingById(Long id);
    BookingResponse getBookingByBookingId(String bookingId);
    BookingResponse cancelBooking(Long id, String currentUserEmail, boolean isAdmin);
}

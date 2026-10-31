package com.voltnav.controller;

import com.voltnav.dto.BookingRequest;
import com.voltnav.dto.BookingResponse;
import com.voltnav.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Slot reservations, QR code issuance, and booking management")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Reserve an EV charging slot (generates unique QR code)")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request,
                                                         Authentication authentication) {
        String userEmail = authentication.getName();
        BookingResponse response = bookingService.createBooking(request, userEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @Operation(summary = "Get current authenticated user's booking history")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.getUserBookings(userEmail));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/reference/{bookingId}")
    @Operation(summary = "Get booking details by reference booking ID")
    public ResponseEntity<BookingResponse> getBookingByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.getBookingByBookingId(bookingId));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation and release the slot")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id,
                                                         Authentication authentication) {
        String userEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(bookingService.cancelBooking(id, userEmail, isAdmin));
    }
}

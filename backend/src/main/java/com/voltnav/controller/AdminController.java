package com.voltnav.controller;

import com.voltnav.dto.*;
import com.voltnav.entity.BookingStatus;
import com.voltnav.service.BookingService;
import com.voltnav.service.DashboardService;
import com.voltnav.service.QrVerificationService;
import com.voltnav.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Admin dashboard statistics, user management, QR verification, and analytics")
public class AdminController {

    private final DashboardService dashboardService;
    private final BookingService bookingService;
    private final UserService userService;
    private final QrVerificationService qrVerificationService;

    public AdminController(DashboardService dashboardService,
                           BookingService bookingService,
                           UserService userService,
                           QrVerificationService qrVerificationService) {
        this.dashboardService = dashboardService;
        this.bookingService = bookingService;
        this.userService = userService;
        this.qrVerificationService = qrVerificationService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics, metrics, and chart series")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getAdminDashboardStats());
    }

    @GetMapping("/bookings")
    @Operation(summary = "Search and filter all reservations across all stations")
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(bookingService.getAllBookings(status, cityId, stationId, bookingDate, query));
    }

    @GetMapping("/users")
    @Operation(summary = "View all registered EV users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "View user details and reservation count")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/users/{id}/toggle-status")
    @Operation(summary = "Toggle user account status (ACTIVE / BLOCKED)")
    public ResponseEntity<UserProfileResponse> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @PostMapping("/qr/verify")
    @Operation(summary = "Scan and verify QR code token, confirming EV check-in")
    public ResponseEntity<QrVerificationResponse> verifyQrCode(
            @Valid @RequestBody QrVerificationRequest request,
            Authentication authentication) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(qrVerificationService.verifyQrToken(request, adminEmail));
    }

    @GetMapping("/qr/check/{token}")
    @Operation(summary = "Inspect booking details and validation status for a QR token")
    public ResponseEntity<QrVerificationResponse> checkQrToken(@PathVariable String token) {
        return ResponseEntity.ok(qrVerificationService.getVerificationDetailsByToken(token));
    }

    @GetMapping("/reports")
    @Operation(summary = "Retrieve comprehensive report metrics")
    public ResponseEntity<AdminDashboardStatsDto> getReports() {
        return ResponseEntity.ok(dashboardService.getAdminDashboardStats());
    }
}

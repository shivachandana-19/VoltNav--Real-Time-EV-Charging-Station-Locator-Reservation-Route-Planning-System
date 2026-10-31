package com.voltnav.controller;

import com.voltnav.dto.PasswordChangeRequest;
import com.voltnav.dto.UserDashboardStatsDto;
import com.voltnav.dto.UserProfileResponse;
import com.voltnav.dto.UserProfileUpdateRequest;
import com.voltnav.service.DashboardService;
import com.voltnav.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Operations", description = "User profile, password change, and personal dashboard")
public class UserController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public UserController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get user dashboard statistics, upcoming bookings, and nearby stations")
    public ResponseEntity<UserDashboardStatsDto> getUserDashboard(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getUserDashboardStats(email));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current authenticated user's profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(email));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user's profile details")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateUserProfile(email, request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change user account password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}

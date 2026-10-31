package com.voltnav.service;

import com.voltnav.dto.AdminDashboardStatsDto;
import com.voltnav.dto.UserDashboardStatsDto;

public interface DashboardService {
    AdminDashboardStatsDto getAdminDashboardStats();
    UserDashboardStatsDto getUserDashboardStats(String userEmail);
}

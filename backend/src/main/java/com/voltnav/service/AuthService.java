package com.voltnav.service;

import com.voltnav.dto.AdminRegisterRequest;
import com.voltnav.dto.AuthResponse;
import com.voltnav.dto.LoginRequest;
import com.voltnav.dto.UserRegisterRequest;

public interface AuthService {
    AuthResponse registerAdmin(AdminRegisterRequest request);
    AuthResponse loginAdmin(LoginRequest request);
    AuthResponse registerUser(UserRegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
}

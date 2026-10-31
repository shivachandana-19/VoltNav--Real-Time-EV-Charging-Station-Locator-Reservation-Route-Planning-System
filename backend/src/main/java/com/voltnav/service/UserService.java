package com.voltnav.service;

import com.voltnav.dto.PasswordChangeRequest;
import com.voltnav.dto.UserProfileResponse;
import com.voltnav.dto.UserProfileUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse getUserById(Long id);
    UserProfileResponse toggleUserStatus(Long id);
    UserProfileResponse getUserProfile(String email);
    UserProfileResponse updateUserProfile(String email, UserProfileUpdateRequest request);
    void changePassword(String email, PasswordChangeRequest request);
}

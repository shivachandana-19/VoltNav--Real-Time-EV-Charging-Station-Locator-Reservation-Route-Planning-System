package com.voltnav.service.impl;

import com.voltnav.dto.AdminRegisterRequest;
import com.voltnav.dto.AuthResponse;
import com.voltnav.dto.LoginRequest;
import com.voltnav.dto.UserRegisterRequest;
import com.voltnav.entity.Admin;
import com.voltnav.entity.Role;
import com.voltnav.entity.User;
import com.voltnav.entity.UserStatus;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceAlreadyExistsException;
import com.voltnav.repository.AdminRepository;
import com.voltnav.repository.UserRepository;
import com.voltnav.security.JwtTokenProvider;
import com.voltnav.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(AdminRepository adminRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           AuthenticationManager authenticationManager) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        if (adminRepository.existsByEmail(request.getEmail()) || userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail().toLowerCase().trim());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPhone(request.getPhone());
        admin.setRole(Role.ROLE_ADMIN);

        Admin savedAdmin = adminRepository.save(admin);
        String token = tokenProvider.generateTokenFromUser(
                savedAdmin.getId(), savedAdmin.getEmail(), savedAdmin.getName(), Role.ROLE_ADMIN.name());

        return new AuthResponse(token, Role.ROLE_ADMIN.name(), savedAdmin.getEmail(), savedAdmin.getName(), savedAdmin.getId());
    }

    @Override
    public AuthResponse loginAdmin(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid admin credentials"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BadCredentialsException("Invalid admin credentials");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, Role.ROLE_ADMIN.name(), admin.getEmail(), admin.getName(), admin.getId());
    }

    @Override
    @Transactional
    public AuthResponse registerUser(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail()) || adminRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateTokenFromUser(
                savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), Role.ROLE_USER.name());

        return new AuthResponse(token, Role.ROLE_USER.name(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getId());
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid user credentials"));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("Your account is blocked. Please contact administrator.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid user credentials");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, Role.ROLE_USER.name(), user.getEmail(), user.getFullName(), user.getId());
    }
}

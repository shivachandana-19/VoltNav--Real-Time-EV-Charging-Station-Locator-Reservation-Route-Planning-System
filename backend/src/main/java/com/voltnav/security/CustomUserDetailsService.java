package com.voltnav.security;

import com.voltnav.entity.Admin;
import com.voltnav.entity.User;
import com.voltnav.entity.UserStatus;
import com.voltnav.repository.AdminRepository;
import com.voltnav.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public CustomUserDetailsService(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First check in Admin
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return new CustomUserDetails(
                    admin.getId(),
                    admin.getEmail(),
                    admin.getPassword(),
                    admin.getName(),
                    admin.getRole().name(),
                    true
            );
        }

        // Check in User
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean active = user.getStatus() == UserStatus.ACTIVE;
            return new CustomUserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFullName(),
                    user.getRole().name(),
                    active
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

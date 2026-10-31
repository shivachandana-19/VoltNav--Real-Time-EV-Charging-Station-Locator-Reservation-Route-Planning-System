package com.voltnav.repository;

import com.voltnav.entity.QrVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QrVerificationRepository extends JpaRepository<QrVerification, Long> {
    Optional<QrVerification> findByQrToken(String qrToken);
    List<QrVerification> findByBookingId(Long bookingId);
}

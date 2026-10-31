package com.voltnav.service.impl;

import com.voltnav.dto.BookingResponse;
import com.voltnav.dto.QrVerificationRequest;
import com.voltnav.dto.QrVerificationResponse;
import com.voltnav.entity.Booking;
import com.voltnav.entity.BookingStatus;
import com.voltnav.entity.QrVerification;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.repository.BookingRepository;
import com.voltnav.repository.QrVerificationRepository;
import com.voltnav.service.BookingService;
import com.voltnav.service.QrVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class QrVerificationServiceImpl implements QrVerificationService {

    private final BookingRepository bookingRepository;
    private final QrVerificationRepository qrVerificationRepository;
    private final BookingService bookingService;

    public QrVerificationServiceImpl(BookingRepository bookingRepository,
                                    QrVerificationRepository qrVerificationRepository,
                                    BookingService bookingService) {
        this.bookingRepository = bookingRepository;
        this.qrVerificationRepository = qrVerificationRepository;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional
    public QrVerificationResponse verifyQrToken(QrVerificationRequest request, String adminEmail) {
        String cleanToken = cleanToken(request.getQrToken());

        Booking booking = bookingRepository.findByQrToken(cleanToken)
                .or(() -> bookingRepository.findByBookingId(cleanToken))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR code or booking token: " + cleanToken));

        BookingResponse bookingResponse = bookingService.getBookingById(booking.getId());

        // Check if already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return new QrVerificationResponse(
                    false,
                    "Reservation has been CANCELLED and is no longer valid.",
                    false,
                    null,
                    null,
                    bookingResponse
            );
        }

        // Check if already checked-in or completed (prevent duplicate reuse)
        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.COMPLETED) {
            Optional<QrVerification> existing = qrVerificationRepository.findByQrToken(booking.getQrToken());
            LocalDateTime verifiedAt = existing.map(QrVerification::getVerifiedAt).orElse(booking.getUpdatedAt());
            String verifiedBy = existing.map(QrVerification::getAdminName).orElse("Administrator");

            return new QrVerificationResponse(
                    false,
                    "QR code was already verified and used on " + verifiedAt + " by " + verifiedBy + ".",
                    true,
                    verifiedAt,
                    verifiedBy,
                    bookingResponse
            );
        }

        // Mark as CHECKED_IN
        booking.setStatus(BookingStatus.CHECKED_IN);
        Booking updatedBooking = bookingRepository.save(booking);

        // Record verification
        QrVerification verification = new QrVerification(
                updatedBooking,
                updatedBooking.getQrToken(),
                true,
                adminEmail != null ? adminEmail : "Admin",
                request.getRemarks() != null ? request.getRemarks() : "Verified via Admin QR Scanner"
        );
        qrVerificationRepository.save(verification);

        BookingResponse updatedResponse = bookingService.getBookingById(updatedBooking.getId());
        return new QrVerificationResponse(
                true,
                "QR Code verified successfully! Welcome " + updatedBooking.getUser().getFullName() + " to " + updatedBooking.getStation().getName() + ".",
                false,
                verification.getVerifiedAt(),
                verification.getAdminName(),
                updatedResponse
        );
    }

    @Override
    public QrVerificationResponse getVerificationDetailsByToken(String qrToken) {
        String cleanToken = cleanToken(qrToken);

        Booking booking = bookingRepository.findByQrToken(cleanToken)
                .or(() -> bookingRepository.findByBookingId(cleanToken))
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with token: " + cleanToken));

        BookingResponse bookingResponse = bookingService.getBookingById(booking.getId());
        Optional<QrVerification> existing = qrVerificationRepository.findByQrToken(booking.getQrToken());

        return new QrVerificationResponse(
                booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.CHECKED_IN,
                "Booking status: " + booking.getStatus(),
                existing.isPresent(),
                existing.map(QrVerification::getVerifiedAt).orElse(null),
                existing.map(QrVerification::getAdminName).orElse(null),
                bookingResponse
        );
    }

    private String cleanToken(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim();
        // If it's a JSON payload scanned from the QR code
        if (trimmed.contains("\"token\":\"")) {
            int start = trimmed.indexOf("\"token\":\"") + 9;
            int end = trimmed.indexOf("\"", start);
            if (start > 8 && end > start) {
                return trimmed.substring(start, end);
            }
        }
        return trimmed;
    }
}

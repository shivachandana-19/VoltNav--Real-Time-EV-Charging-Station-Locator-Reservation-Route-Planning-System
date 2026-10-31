package com.voltnav.dto;

import java.time.LocalDateTime;

public class QrVerificationResponse {

    private boolean success;
    private String message;
    private boolean alreadyVerified;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private BookingResponse booking;

    public QrVerificationResponse() {
    }

    public QrVerificationResponse(boolean success, String message, boolean alreadyVerified,
                                  LocalDateTime verifiedAt, String verifiedBy, BookingResponse booking) {
        this.success = success;
        this.message = message;
        this.alreadyVerified = alreadyVerified;
        this.verifiedAt = verifiedAt;
        this.verifiedBy = verifiedBy;
        this.booking = booking;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAlreadyVerified() {
        return alreadyVerified;
    }

    public void setAlreadyVerified(boolean alreadyVerified) {
        this.alreadyVerified = alreadyVerified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public BookingResponse getBooking() {
        return booking;
    }

    public void setBooking(BookingResponse booking) {
        this.booking = booking;
    }
}

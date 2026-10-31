package com.voltnav.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_verifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QrVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "qr_token", nullable = false, length = 100)
    private String qrToken;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = true;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @Column(name = "admin_name", length = 100)
    private String adminName;

    @Column(length = 255)
    private String remarks;

    public QrVerification() {
    }

    public QrVerification(Booking booking, String qrToken, Boolean isVerified, String adminName, String remarks) {
        this.booking = booking;
        this.qrToken = qrToken;
        this.isVerified = isVerified != null ? isVerified : true;
        this.verifiedAt = LocalDateTime.now();
        this.adminName = adminName;
        this.remarks = remarks;
    }

    @PrePersist
    protected void onCreate() {
        if (this.verifiedAt == null) {
            this.verifiedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

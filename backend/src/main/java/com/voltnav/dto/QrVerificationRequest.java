package com.voltnav.dto;

import jakarta.validation.constraints.NotBlank;

public class QrVerificationRequest {

    @NotBlank(message = "QR Token is required")
    private String qrToken;

    private String remarks;

    public QrVerificationRequest() {
    }

    public QrVerificationRequest(String qrToken, String remarks) {
        this.qrToken = qrToken;
        this.remarks = remarks;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

package com.voltnav.service;

import com.voltnav.dto.QrVerificationRequest;
import com.voltnav.dto.QrVerificationResponse;

public interface QrVerificationService {
    QrVerificationResponse verifyQrToken(QrVerificationRequest request, String adminEmail);
    QrVerificationResponse getVerificationDetailsByToken(String qrToken);
}

package com.example.utmentor.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

@Service
public class OtpService {
    private final ConcurrentMap<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private static final int OTP_EXPIRY_MINUTES = 10;

    public String generateOtp(String email) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));
        
        // Store OTP with expiry time
        Instant expiryTime = Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60);
        otpStorage.put(email, new OtpData(otp, expiryTime));
        
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);
        
        if (otpData == null) {
            return false; // OTP not found
        }
        
        if (Instant.now().isAfter(otpData.expiryTime)) {
            otpStorage.remove(email); // Clean up expired OTP
            return false; // OTP expired
        }
        
        if (otpData.otp.equals(otp)) {
            otpStorage.remove(email); // Remove OTP after successful validation
            return true;
        }
        
        return false; // Invalid OTP
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
    }

    public boolean hasValidOtp(String email) {
        OtpData otpData = otpStorage.get(email);
        if (otpData == null) {
            return false;
        }
        
        if (Instant.now().isAfter(otpData.expiryTime)) {
            otpStorage.remove(email);
            return false;
        }
        
        return true;
    }

    private static class OtpData {
        final String otp;
        final Instant expiryTime;

        OtpData(String otp, Instant expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}

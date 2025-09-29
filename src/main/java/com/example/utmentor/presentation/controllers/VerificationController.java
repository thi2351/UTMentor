package com.example.utmentor.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.webModels.otp.ResendOtpRequest;
import com.example.utmentor.models.webModels.otp.VerifyEmailRequest;
import com.example.utmentor.models.webModels.otp.VerifyEmailResponse;
import com.example.utmentor.services.AuthService;
import com.example.utmentor.services.OtpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/verification")
public class VerificationController {
    OtpService _otpService;
    public VerificationController(AuthService service, OtpService otpService) {
        this._otpService = otpService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("activate-account")
    @Async
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        // _otpService.generateOtp(request.email());

        boolean success =  _otpService.validateOtp(request.email(), request.otp());
        VerifyEmailResponse response = new VerifyEmailResponse(success);

        if (success) {
            //Bật isActive, fetch data từ dưới datacore lên user
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("resend")
    public void resendEmail(@Valid @RequestBody ResendOtpRequest request) {
        _otpService.generateOtp(request.email());
    }
}

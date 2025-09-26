package com.example.utmentor.presentation.controllers;

import com.example.utmentor.models.webModels.otp.ResendOtpRequest;
import com.example.utmentor.models.webModels.otp.VerifyEmailRequest;
import com.example.utmentor.models.webModels.otp.VerifyEmailResponse;
import com.example.utmentor.services.OtpService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.models.webModels.users.LoginRequest;
import com.example.utmentor.models.webModels.users.LoginResponse;
import com.example.utmentor.services.AuthService;

import jakarta.validation.Valid;


@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService _service;
    private final OtpService _otpService;
    public AuthController(AuthService service, OtpService otpService) {
        this._service = service;
        this._otpService = otpService;
    }

    @PostMapping("register")
    public ResponseEntity<CreateUserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse result = _service.createUser(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("verify")
    @Async
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        _otpService.generateOtp(request.email());

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

    @PermitAll
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = _service.login(request);
        return ResponseEntity.ok(result);
    }

}

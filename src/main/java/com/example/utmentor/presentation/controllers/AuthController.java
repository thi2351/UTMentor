package com.example.utmentor.presentation.controllers;

import com.example.utmentor.infrastructures.securities.JwtService;
import com.example.utmentor.models.webModels.otp.ResendOtpRequest;
import com.example.utmentor.models.webModels.otp.VerifyEmailRequest;
import com.example.utmentor.models.webModels.otp.VerifyEmailResponse;
import com.example.utmentor.services.OtpService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Value;
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
import com.example.utmentor.util.UserUtils;

import jakarta.validation.Valid;
import com.example.utmentor.models.docEntities.users.User;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

@CrossOrigin(
        origins = {"http://localhost:5173"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService _service;
    private final OtpService _otpService;
    private final JwtService _jwtService;
    @Value("${fakerefresh}") String secret;
    public AuthController(AuthService service, OtpService otpService, JwtService jwtService) {
        this._service = service;
        this._otpService = otpService;
        this._jwtService = jwtService;
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
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        User result = _service.login(request);

        Map<String, Object> claims = new HashMap<>();
        if (result.getRole() != null) {
            claims.put("role", result.getRole().name());
        }

        // Generate tokens
        String accessToken = _jwtService.generateToken(result.getId(), claims);
//        String refreshToken = _jwtService.generateRefreshToken(result.getId());

        // Build HttpOnly cookies
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)            // use true in production with HTTPS
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60)         // 15 minutes
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", secret)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(14 * 24 * 60 * 60) // 14 days
                .build();

        // Attach cookies to the response
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // Build DTO (without tokens in JSON)
        LoginResponse loginResponse = UserUtils.pickUser(result);

        return ResponseEntity.ok(loginResponse);
    }

}

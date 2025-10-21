package com.example.utmentor.presentation.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.infrastructures.securities.JwtService;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.models.webModels.users.LoginRequest;
import com.example.utmentor.models.webModels.users.LoginResponse;
import com.example.utmentor.services.AuthService;
import com.example.utmentor.services.OtpService;
import com.example.utmentor.services.UserService;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
@CrossOrigin(
        origins = {"http://localhost:5173"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService _authService;
//    private final OtpService _otpService;
    private final JwtService _jwtService;
    private final UserService _userService;
    
    public AuthController(AuthService service,
                          OtpService otpService,
                          JwtService jwtService,
                          UserService userService) {
        this._authService = service;
//        this._otpService = otpService;
        this._jwtService = jwtService;
        this._userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<CreateUserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse result = _authService.register(request);

        return ResponseEntity.ok(result);
    }

//    @PostMapping("verify")
//    public ResponseEntity<VerifyEmailResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
//        _otpService.generateOtp(request.email());
//
//        boolean success =  _otpService.validateOtp(request.email(), request.otp());
//        VerifyEmailResponse response = new VerifyEmailResponse(success);
//
//        if (success) {
//            //Bật isActive, fetch data từ dưới datacore lên user
//        }
//
//        return ResponseEntity.ok(response);
//    }


//    @PostMapping("resend")
//    public void resendEmail(@Valid @RequestBody ResendOtpRequest request) {
//        _otpService.generateOtp(request.email());
//    }

    @PermitAll
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        User result = _authService.login(request);

        //Generate accessToken
        Map<String, Object> accessClaim = _authService.createAcessTokenClaim(result);
        String accessToken = _jwtService.generateToken(result.getId(), accessClaim);
        //Generate refreshToken
        Map<String, Object> refreshClaims = _authService.createRefreshTokenClaim(result);
        String refreshToken = _jwtService.generateToken(result.getId(), refreshClaims);


        // Build HttpOnly cookies
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15)         // 15 minutes
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(14 * 24 * 60 * 60) // 14 days
                .build();

        // Attach cookies to the response
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Build response with tokens
        LoginResponse loginResponse = LoginResponse.builder()
                .user(result)
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("refresh")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Extract refresh token from cookie
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        
        if (refreshToken == null || !_jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Extract user info from refresh token
        String userId = _jwtService.extractSubject(refreshToken);
        // Refresh token contains userId as subject
        var userOpt = _userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userOpt.get();
        
        // Generate new access token
        Map<String, Object> claims = _authService.createAcessTokenClaim(user);
        String newAccessToken = _jwtService.generateToken(user.getId(), claims);
        
        // Update access token cookie
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60) // 15 minutes
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
        
        // Return new access token in response
        LoginResponse loginResponse = LoginResponse.builder()
                .user(user)
                .accessToken(newAccessToken)
                .build();
        
        return ResponseEntity.ok(loginResponse);
    }

    @PermitAll
    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear both access and refresh token cookies
        ResponseCookie clearAccessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        
        ResponseCookie clearRefreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());
        
        return ResponseEntity.ok().build();
    }

}

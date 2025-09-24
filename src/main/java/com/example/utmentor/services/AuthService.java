package com.example.utmentor.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.DatacoreRepository;
import com.example.utmentor.infrastructures.repository.UserRepository;
import com.example.utmentor.infrastructures.securities.JwtService;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.models.webModels.users.LoginRequest;
import com.example.utmentor.models.webModels.users.LoginResponse;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

@Service
public class AuthService {
    private final UserRepository _repository;
    private final DatacoreRepository _datacore;
    private final PasswordEncoder _encoder;
    private final JwtService _jwtService;
    private final OtpService _otpService;
    private final EmailService _emailService;
    @Value("${fakerefresh}") String secret;
    
    AuthService(UserRepository repository, DatacoreRepository datacore, PasswordEncoder encoder, 
                JwtService jwtService, OtpService otpService, EmailService emailService) {
        this._repository = repository;
        this._datacore = datacore;
        this._encoder = encoder;
        this._jwtService = jwtService;
        this._otpService = otpService;
        this._emailService = emailService;
    }

    public static String getLocalPart(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 0) return null;
        return email.substring(0, at);
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        //Validation: Unique studentID, studentEmail, username
        ValidatorException ex = new ValidatorException("Register request failed.");

        if (!_datacore.existsByStudentEmail(request.studentEmail())) {
            ex.add(Errors.DATACORE_NOT_FOUND);
        }

        if (ex.hasAny()) throw ex;

        if (_repository.existsByStudentEmail(request.studentEmail())) {
            ex.add(Errors.EMAIL_EXISTS);
        }

        if (ex.hasAny()) throw ex;

        //CreateUser
        String username = getLocalPart(request.studentEmail());
        String passwordHashed = _encoder.encode(request.password());
        var user = new User(
                UUID.randomUUID().toString(),
                null,
                null,
                null,
                Role.STUDENT,
                request.studentEmail(),
                username,
                passwordHashed,
                null,
                null

        );

        _repository.save(user);
        return new CreateUserResponse(username);
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.username().trim();
        String rawPassword = request.password();

        var userOpt = _repository.findByUsername(username);
        if (userOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("Login failed.");
            vex.add(Errors.INVALID_CREDENTIALS);
            vex.setHttpCode(HttpStatus.UNAUTHORIZED);
            throw vex;
        }

        var user = userOpt.get();
        boolean matches = _encoder.matches(rawPassword, user.getPasswordHash());
        if (!matches) {
            ValidatorException vex = new ValidatorException("Login failed.");
            vex.add(Errors.INVALID_CREDENTIALS);
            vex.setHttpCode(HttpStatus.UNAUTHORIZED);
            throw vex;
        }

        Map<String, Object> claims = new HashMap<>();
        if (user.getRole() != null) {
            claims.put("role", user.getRole().name());
        }

        String token = _jwtService.generateToken(user.getId(), claims);
        return new LoginResponse(token, secret);
    }
}

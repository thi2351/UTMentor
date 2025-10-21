package com.example.utmentor.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.Interface.DatacoreRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.models.webModels.users.LoginRequest;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

@Service
public class AuthService {
    private final UserRepository _repository;
    private final DatacoreRepository _datacore;
    private final PasswordEncoder _encoder;
//    private final JwtService _jwtService;
//    private final OtpService _otpService;
//    private final EmailService _emailService;
//    @Value("${fakerefresh}") String secret;
    
    public AuthService(UserRepository repository, DatacoreRepository datacore, PasswordEncoder encoder) {
        this._repository = repository;
        this._datacore = datacore;
        this._encoder = encoder;
//        this._jwtService = jwtService;
//        this._otpService = otpService;
//        this._emailService = emailService;
    }

    public static String getLocalPart(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 0) return null;
        return email.substring(0, at);
    }

    public CreateUserResponse register(CreateUserRequest request) {
        //Validation: Unique studentID, email, username
        ValidatorException ex = new ValidatorException("Register request failed.");

        if (!_datacore.existsByEmail(request.email())) {
            ex.add(Errors.DATACORE_NOT_FOUND);
        }

        if (ex.hasAny()) throw ex;

        String username = getLocalPart(request.email());
        if (_repository.existsByUsername(username)) {
            ex.add(Errors.USERNAME_EXISTS);
        }

        if (ex.hasAny()) throw ex;

        var obj= _datacore.findByEmail(request.email()).get();
        String passwordHashed = _encoder.encode(request.password());
        var user = new User(
                UUID.randomUUID().toString(),
                obj.getFirstName(),
                obj.getLastName(),
                null,
                obj.getRole(), // User starts with STUDENT role
                username,
                null,
                passwordHashed
        );

        _repository.save(user);
        return new CreateUserResponse(username);
    }

    public User login(LoginRequest request) {
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

        return user;
    }

    public Map<String, Object> createAcessTokenClaim(User user) {
        Map<String, Object> claims = new HashMap<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::name)
                    .toList();
            claims.put("roles", roleNames);
        }
        return claims;
    }

    public Map<String, Object> createRefreshTokenClaim(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("userId", user.getId());
        return claims;
    }
}


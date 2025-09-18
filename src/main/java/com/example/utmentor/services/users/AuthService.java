package com.example.utmentor.services.users;

import com.example.utmentor.infrastructures.repository.UserRepository;
import com.example.utmentor.infrastructures.securities.JwtService;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.User;
import com.example.utmentor.util.Errors;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.models.webModels.users.LoginRequest;
import com.example.utmentor.models.webModels.users.LoginResponse;
import com.example.utmentor.util.ValidatorException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository _repository;
    private final PasswordEncoder _encoder;
    private final JwtService _jwtService;
    AuthService(UserRepository repository, PasswordEncoder encoder, JwtService jwtService) {
        this._repository = repository;
        this._encoder = encoder;
        this._jwtService = jwtService;
    }


    public CreateUserResponse createUser(CreateUserRequest request) {
        //Validation: Unique studentID, studentEmail, username
        ValidatorException ex = new ValidatorException("Register request failed.");

        if (_repository.existsByUsername(request.username()))
            ex.add(Errors.USERNAME_EXISTS);
        if (_repository.existsByStudentID(request.studentID()))
            ex.add(Errors.STUDENTID_EXISTS);

        if (ex.hasAny()) throw ex;

        String passwordHashed = _encoder.encode(request.password());
        User user = new User(
                UUID.randomUUID().toString(),
                request.firstName(),
                request.lastName(),
                null,
                Role.STUDENT,
                request.studentID(),
                request.studentEmail(),
                request.username(),
                passwordHashed,
                null,
                null
        );

        _repository.save(user);
        return new CreateUserResponse(request.username(), request.studentID(), request.studentEmail());
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
        String token = _jwtService.generateToken(user.getUsername(), claims);
        String fullName = user.getFirstName() + " " + user.getLastName();
        String role = user.getRole() != null ? user.getRole().name() : null;
        return new LoginResponse(user.getUsername(), role, fullName, token);
    }
}

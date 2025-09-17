package com.example.utmentor.services.users;

import com.example.utmentor.infrastructures.repository.UserRepository;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.User;
import com.example.utmentor.models.exceptions.GlobalResult;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository _repository;
    private final PasswordEncoder _encoder;
    AuthService(UserRepository repository, PasswordEncoder encoder) {
        this._repository = repository;
        this._encoder = encoder;
    }


    public GlobalResult<CreateUserResponse> createUser(CreateUserRequest request) {
        //Validation: Unique studentID, studentEmail, username
        var errors = new HashMap<String, String>();

        if (_repository.existsByUsername(request.username()))
            errors.put("UsernameExisted", "Username already exists.");
        if (_repository.existsByStudentID(request.studentID()))
            errors.put("StudentIDExisted", "StudentID already exists.");

        if (!errors.isEmpty()) return GlobalResult.badRequest(errors);

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
        var response = new CreateUserResponse("EEEEE");
        return new GlobalResult<CreateUserResponse>(response,new HashMap<String, String>(), HttpStatus.OK);
    }
}

package com.example.utmentor.metadata;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.utmentor.infrastructures.repository.Interface.DatacoreRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.HCMUT_DATACORE.Datacore;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.util.ValidatorException;

/**
 * Component responsible for creating the admin user during bootstrap.
 */
@Component
public class AdminBootstrap {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatacoreRepository datacoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BootstrapHelper helper;

    @Value("${admin.bootstrap.email:admin@hcmut.edu.vn}")
    private String adminEmail;

    @Value("${admin.bootstrap.password:admin123}")
    private String adminPassword;

    /**
     * Create the admin user if it doesn't exist.
     * 
     * @return true if admin was created, false if it already exists
     * @throws ValidatorException if email format is invalid
     */
    public boolean createAdmin() throws ValidatorException {
        String adminUsername = helper.getLocalPart(adminEmail);
        if (adminUsername == null) {
            throw new ValidatorException("Invalid admin email format");
        }

        if (userRepository.existsByUsername(adminUsername)) {
            System.out.println("Admin user already exists. Skipping bootstrap.");
            return false;
        }

        // Create admin profile in HCMUT_DATACORE
        Datacore adminProfile = new Datacore(
                "ADMIN-001",
                "System",
                "Administrator",
                Department.CS,
                List.of(Role.ADMIN),
                adminEmail,
                null, // studentProfile
                null  // tutorProfile
        );

        datacoreRepository.save(adminProfile);

        // Create admin user with multiple roles
        String passwordHash = passwordEncoder.encode(adminPassword);
        User adminUser = new User(
                UUID.randomUUID().toString(),
                "System",
                "Administrator",
                Department.CS,
                List.of(Role.ADMIN, Role.AFFAIR),
                adminUsername,
                null, // avatarUrl
                passwordHash
        );

        userRepository.save(adminUser);

        System.out.println("==========================================");
        System.out.println("ADMIN USER CREATED SUCCESSFULLY!");
        System.out.println("Email: " + adminEmail);
        System.out.println("Username: " + adminUsername);
        System.out.println("Password: " + adminPassword);
        System.out.println("==========================================");
        System.out.println("IMPORTANT: Change the admin password after first login!");
        System.out.println("==========================================");

        return true;
    }
}


package com.example.utmentor.services;

import java.util.UUID;

import com.example.utmentor.models.docEntities.HCMUT_DATACORE.Datacore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.DatacoreRepository;
import com.example.utmentor.infrastructures.repository.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;

@Service
public class AdminBootstrapService implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DatacoreRepository datacoreRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.bootstrap.email:admin@hcmut.edu.vn}")
    private String adminEmail;

    @Value("${admin.bootstrap.password:admin123}")
    private String adminPassword;

    @Value("${admin.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    public AdminBootstrapService(UserRepository userRepository,
                                 DatacoreRepository datacoreRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.datacoreRepository = datacoreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!bootstrapEnabled) {
            return;
        }

        // Check if admin already exists
        String adminUsername = getLocalPart(adminEmail);
        if (userRepository.existsByUsername(adminUsername)) {
            System.out.println("Admin user already exists. Skipping bootstrap.");
            return;
        }

        // Create admin profile in HCMUT_DATACORE
        Datacore adminProfile = new Datacore(
                "ADMIN-001",
                "System",
                "Administrator",
                Department.CS,
                Role.ADMIN,
                adminEmail,
                // username - not needed for datacore
                // passwordHash - not needed for datacore
                null, // studentProfile
                null  // tutorProfile
        );

        // Save admin profile to datacore
        datacoreRepository.save(adminProfile);

        // Create admin user
        String passwordHash = passwordEncoder.encode(adminPassword);
        User adminUser = new User(
                UUID.randomUUID().toString(),
                "System",
                "Administrator",
                Department.CS,
                Role.ADMIN,
                adminEmail,
                adminUsername,
                passwordHash,
                null, // studentProfile
                null  // tutorProfile
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
    }

    private String getLocalPart(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 0) return null;
        return email.substring(0, at);
    }
}

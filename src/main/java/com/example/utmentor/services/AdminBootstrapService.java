package com.example.utmentor.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.metadata.AdminBootstrap;
import com.example.utmentor.metadata.ReviewBootstrap;
import com.example.utmentor.metadata.UserBootstrap;
import com.example.utmentor.util.ValidatorException;

/**
 * Service that orchestrates the bootstrap process during application startup.
 * Delegates to specialized bootstrap components for different data types.
 * 
 * Bootstrap will only run if no users exist in the database.
 */
@Service
public class AdminBootstrapService implements CommandLineRunner {

    @Autowired
    private AdminBootstrap adminBootstrap;

    @Autowired
    private UserBootstrap userBootstrap;

    @Autowired
    private ReviewBootstrap reviewBootstrap;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!bootstrapEnabled) {
            return;
        }

        // Check if any user already exists - if yes, skip bootstrap
        long userCount = userRepository.count();
        if (userCount > 0) {
            System.out.println("==========================================");
            System.out.println("Bootstrap skipped: Database already contains " + userCount + " user(s)");
            System.out.println("==========================================");
            return;
        }

        try {
            System.out.println("==========================================");
            System.out.println("Starting bootstrap process...");
            System.out.println("==========================================");

            // Create admin user
            adminBootstrap.createAdmin();

            // Create virtual tutors
            userBootstrap.createVirtualTutors();

            // Create virtual students
            userBootstrap.createVirtualStudents();

            // Create bootstrap reviews
            reviewBootstrap.createBootstrapReviews();

            System.out.println("==========================================");
            System.out.println("Bootstrap completed successfully!");
            System.out.println("==========================================");

        } catch (ValidatorException e) {
            System.err.println("Validation error during admin bootstrap: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during admin bootstrap: " + e.getMessage());
            throw new RuntimeException("Failed to bootstrap application", e);
        }
    }
}

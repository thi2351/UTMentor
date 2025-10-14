package com.example.utmentor.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.Interface.DatacoreRepository;
import com.example.utmentor.infrastructures.repository.Interface.StudentProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.TutorProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.docEntities.HCMUT_DATACORE.Datacore;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.util.ValidatorException;

@Service
public class AdminBootstrapService implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DatacoreRepository datacoreRepository;
    private final TutorProfileRepository tutorProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.bootstrap.email:admin@hcmut.edu.vn}")
    private String adminEmail;

    @Value("${admin.bootstrap.password:admin123}")
    private String adminPassword;

    @Value("${admin.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    public AdminBootstrapService(UserRepository userRepository,
                                 DatacoreRepository datacoreRepository,
                                 TutorProfileRepository tutorProfileRepository,
                                 StudentProfileRepository studentProfileRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.datacoreRepository = datacoreRepository;
        this.tutorProfileRepository = tutorProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!bootstrapEnabled) {
            return;
        }

        try {
            // Check if admin already exists
            String adminUsername = getLocalPart(adminEmail);
            if (adminUsername == null) {
                throw new ValidatorException("Invalid admin email format");
            }
            
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
                    List.of(Role.ADMIN),
                    adminEmail,
                    null, // studentProfile
                    null  // tutorProfile
            );

            // Save admin profile to datacore
            datacoreRepository.save(adminProfile);

            // Create admin user with multiple roles
            String passwordHash = passwordEncoder.encode(adminPassword);
            User adminUser = new User(
                    UUID.randomUUID().toString(),
                    "System",
                    "Administrator",
                    Department.CS,
                    List.of(Role.ADMIN, Role.AFFAIR), // Admin can have multiple roles
                    adminUsername,
                    null, // avatarUrl
                    passwordHash
            );

            userRepository.save(adminUser);

            // Create virtual tutors
            createVirtualTutors();
            
            // Create virtual student
            createVirtualStudent();

            System.out.println("==========================================");
            System.out.println("ADMIN USER CREATED SUCCESSFULLY!");
            System.out.println("Email: " + adminEmail);
            System.out.println("Username: " + adminUsername);
            System.out.println("Password: " + adminPassword);
            System.out.println("==========================================");
            System.out.println("IMPORTANT: Change the admin password after first login!");
            System.out.println("==========================================");
        } catch (ValidatorException e) {
            System.err.println("Validation error during admin bootstrap: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during admin bootstrap: " + e.getMessage());
            throw new RuntimeException("Failed to create admin user", e);
        }
    }

    private String getLocalPart(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 0) return null;
        return email.substring(0, at);
    }

    private void createVirtualTutors() {
        try {
            System.out.println("Creating virtual tutors...");
            
            // Virtual Tutor 1: Computer Science - AI/ML Expert
            createVirtualTutor(
                "tutor001", "Nguyen Van An", "an.nguyen@hcmut.edu.vn", Department.CS,
                List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.MACHINE_LEARNING, Expertise.DATA_SCIENCE),
                5, 3, 4.8
            );
            
            // Virtual Tutor 2: Computer Science - Software Engineering
            createVirtualTutor(
                "tutor002", "Tran Thi Binh", "binh.tran@hcmut.edu.vn", Department.CS,
                List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN),
                4, 2, 4.6
            );
            
            // Virtual Tutor 3: Computer Engineering - Embedded Systems
            createVirtualTutor(
                "tutor003", "Le Van Cuong", "cuong.le@hcmut.edu.vn", Department.CE,
                List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS),
                6, 4, 4.9
            );
            
            // Virtual Tutor 4: Electronic Engineering - Cybersecurity
            createVirtualTutor(
                "tutor004", "Pham Thi Dung", "dung.pham@hcmut.edu.vn", Department.EE,
                List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS),
                3, 1, 4.7
            );
            
            // Virtual Tutor 5: Computer Science - Mobile Development
            createVirtualTutor(
                "tutor005", "Hoang Van Em", "em.hoang@hcmut.edu.vn", Department.CS,
                List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING),
                4, 2, 4.5
            );
            
            // Virtual Tutor 6: Mechanical Engineering
            createVirtualTutor(
                "tutor006", "Vo Thi Phuong", "phuong.vo@hcmut.edu.vn", Department.ME,
                List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN),
                5, 3, 4.4
            );
            
            // Virtual Tutor 7: Chemical Engineering
            createVirtualTutor(
                "tutor007", "Dang Van Quang", "quang.dang@hcmut.edu.vn", Department.CH,
                List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE),
                4, 2, 4.6
            );
            
            // Virtual Tutor 8: Computer Science - Algorithms Expert
            createVirtualTutor(
                "tutor008", "Bui Thi Hoa", "hoa.bui@hcmut.edu.vn", Department.CS,
                List.of(Expertise.ALGORITHMS, Expertise.DATA_SCIENCE, Expertise.ARTIFICIAL_INTELLIGENCE),
                6, 4, 4.9
            );
            
            // Virtual Tutor 9: Electronic Engineering - Control Systems
            createVirtualTutor(
                "tutor009", "Ngo Van Inh", "inh.ngo@hcmut.edu.vn", Department.EE,
                List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS),
                5, 3, 4.7
            );
            
            // Virtual Tutor 10: Computer Science - Full Stack Developer
            createVirtualTutor(
                "tutor010", "Do Thi Kim", "kim.do@hcmut.edu.vn", Department.CS,
                List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING),
                4, 2, 4.5
            );
            
            System.out.println("Virtual tutors created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating virtual tutors: " + e.getMessage());
            // Don't throw exception to avoid breaking admin creation
        }
    }
    
    private void createVirtualTutor(String tutorId, String fullName, String email, 
                                  Department department, List<Expertise> expertise,
                                  int maxCapacity, int currentMentees, double rating) {
        try {
            // Create user profile
            String passwordHash = passwordEncoder.encode("tutor123");
            String username = getLocalPart(email);
            
            User tutorUser = new User(
                tutorId,
                fullName.split(" ")[0], // firstName
                fullName.split(" ")[fullName.split(" ").length - 1], // lastName
                department,
                List.of(Role.TUTOR),
                username,
                null, // avatarUrl
                passwordHash
            );
            
            userRepository.save(tutorUser);
            
            // Create datacore profile
            Datacore tutorDatacore = new Datacore(
                "TUTOR-" + tutorId.substring(tutorId.length() - 3),
                fullName.split(" ")[0],
                fullName.split(" ")[fullName.split(" ").length - 1],
                department,
                List.of(Role.TUTOR),
                email,
                null, // studentProfile
                null  // tutorProfile - will be set after creating TutorProfile
            );
            
            datacoreRepository.save(tutorDatacore);
            
            // Create tutor profile
            TutorProfile tutorProfile = new TutorProfile(
                tutorId,
                expertise,
                true, // isActive
                maxCapacity,
                currentMentees
            );
            
            // Set rating information
            tutorProfile.setRatingCount((int)(rating * 10)); // Simulate rating count
            tutorProfile.setRatingAvg(rating);
            
            tutorProfileRepository.save(tutorProfile);
            
            System.out.println("Created virtual tutor: " + fullName + " (" + email + ")");
            
        } catch (Exception e) {
            System.err.println("Error creating tutor " + tutorId + ": " + e.getMessage());
        }
    }
    
    private void createVirtualStudent() {
        try {
            System.out.println("Creating virtual student...");
            
            // Virtual Student: Computer Science Student
            createVirtualStudent(
                "student001", "Tran Thi Lan", "lan.tran@hcmut.edu.vn", Department.CS,
                "20123456"
            );
            
            System.out.println("Virtual student created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating virtual student: " + e.getMessage());
            // Don't throw exception to avoid breaking admin creation
        }
    }
    
    private void createVirtualStudent(String studentId, String fullName, String email, 
                                   Department department, String studentID) {
        try {
            // Create user profile
            String passwordHash = passwordEncoder.encode("student123");
            String username = getLocalPart(email);
            
            User studentUser = new User(
                studentId,
                fullName.split(" ")[0], // firstName
                fullName.split(" ")[fullName.split(" ").length - 1], // lastName
                department,
                List.of(Role.STUDENT),
                username,
                null, // avatarUrl
                passwordHash
            );
            
            userRepository.save(studentUser);
            
            // Create datacore profile
            Datacore studentDatacore = new Datacore(
                "STUDENT-" + studentId.substring(studentId.length() - 3),
                fullName.split(" ")[0],
                fullName.split(" ")[fullName.split(" ").length - 1],
                department,
                List.of(Role.STUDENT),
                email,
                null, // studentProfile - will be set after creating StudentProfile
                null  // tutorProfile
            );
            
            datacoreRepository.save(studentDatacore);
            
            // Create student profile
            StudentProfile studentProfile = new StudentProfile(
                studentId,
                studentID,
                true // isActive
            );
            
            studentProfileRepository.save(studentProfile);
            
            System.out.println("Created virtual student: " + fullName + " (" + email + ") - Student ID: " + studentID);
            
        } catch (Exception e) {
            System.err.println("Error creating student " + studentId + ": " + e.getMessage());
        }
    }
}

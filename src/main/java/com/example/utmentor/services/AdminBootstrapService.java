package com.example.utmentor.services;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;

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

            // Computer Science Department (30 tutors)
            createVirtualTutor("tutor001", "Nguyễn Văn An", "an.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 5, 3, 4.8);
            createVirtualTutor("tutor002", "Trần Thị Bình", "binh.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN), 4, 2, 4.6);
            createVirtualTutor("tutor003", "Lê Văn Cường", "cuong.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.DATA_SCIENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 6, 4, 4.9);
            createVirtualTutor("tutor004", "Phạm Thị Dung", "dung.pham@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS), 3, 1, 4.7);
            createVirtualTutor("tutor005", "Hoàng Văn Em", "em.hoang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.5);
            createVirtualTutor("tutor006", "Võ Thị Phương", "phuong.vo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 5, 3, 4.7);
            createVirtualTutor("tutor007", "Đặng Văn Quang", "quang.dang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE, Expertise.ALGORITHMS), 6, 4, 4.8);
            createVirtualTutor("tutor008", "Bùi Thị Hòa", "hoa.bui@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE), 5, 2, 4.6);
            createVirtualTutor("tutor009", "Ngô Văn Ính", "inh.ngo@hcmut.edu.vn", Department.CS, // Note: Ính is less common, ensure it's correct
                    List.of(Expertise.DATABASE_DESIGN, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 3, 4.5);
            createVirtualTutor("tutor010", "Đỗ Thị Kim", "kim.do@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 5, 2, 4.7);
            createVirtualTutor("tutor011", "Trương Văn Long", "long.truong@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS), 6, 5, 4.9);
            createVirtualTutor("tutor012", "Nguyễn Thị Mai", "mai.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.6);
            createVirtualTutor("tutor013", "Phan Văn Nam", "nam.phan@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.MOBILE_DEVELOPMENT, Expertise.DATABASE_DESIGN), 5, 3, 4.7);
            createVirtualTutor("tutor014", "Lê Thị Oanh", "oanh.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATA_SCIENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 4, 2, 4.5);
            createVirtualTutor("tutor015", "Trần Văn Phúc", "phuc.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ALGORITHMS), 6, 4, 4.8);
            createVirtualTutor("tutor016", "Hoàng Thị Quỳnh", "quynh.hoang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.DATABASE_DESIGN), 5, 3, 4.6);
            createVirtualTutor("tutor017", "Nguyễn Văn Sang", "sang.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.ALGORITHMS, Expertise.COMPUTER_NETWORKS), 4, 2, 4.7);
            createVirtualTutor("tutor018", "Phạm Thị Thảo", "thao.pham@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT), 5, 2, 4.5);
            createVirtualTutor("tutor019", "Lê Văn Uyên", "uyen.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 6, 5, 4.9);
            createVirtualTutor("tutor020", "Trần Thị Vân", "van.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 4, 3, 4.6);
            createVirtualTutor("tutor021", "Nguyễn Văn Xuân", "xuan.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING, Expertise.ALGORITHMS), 5, 2, 4.7);
            createVirtualTutor("tutor022", "Võ Thị Yến", "yen.vo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 6, 4, 4.8);
            createVirtualTutor("tutor023", "Đặng Văn An", "an.dang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.5);
            createVirtualTutor("tutor024", "Bùi Thị Bảo", "bao.bui@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATA_SCIENCE, Expertise.ALGORITHMS), 5, 3, 4.6);
            createVirtualTutor("tutor025", "Ngô Văn Cao", "cao.ngo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.7);
            createVirtualTutor("tutor026", "Đỗ Thị Đào", "dao.do@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 5, 3, 4.5);
            createVirtualTutor("tutor027", "Trương Văn Hải", "hai.truong@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 6, 5, 4.9);
            createVirtualTutor("tutor028", "Nguyễn Thị Huệ", "hue.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.ARTIFICIAL_INTELLIGENCE), 4, 2, 4.6);
            createVirtualTutor("tutor029", "Phan Văn Khoa", "khoa.phan@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS), 5, 3, 4.8);
            createVirtualTutor("tutor030", "Lê Thị Lan", "lan.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.WEB_DEVELOPMENT), 4, 2, 4.5);

            // Computer Engineering Department (20 tutors)
            createVirtualTutor("tutor031", "Trần Văn Minh", "minh.tran@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor032", "Hoàng Thị Nga", "nga.hoang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor033", "Nguyễn Văn Phong", "phong.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6);
            createVirtualTutor("tutor034", "Phạm Thị Quỳnh", "quynh.pham@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.8);
            createVirtualTutor("tutor035", "Lê Văn Tâm", "tam.le@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9);
            createVirtualTutor("tutor036", "Trần Thị Uyên", "uyen.tran@hcmut.edu.vn", Department.CE,
                    List.of( Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.5);
            createVirtualTutor("tutor037", "Nguyễn Văn Vinh", "vinh.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor038", "Võ Thị Xuân", "xuan.vo@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.6);
            createVirtualTutor("tutor039", "Đặng Văn Yến", "yen.dang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS), 6, 4, 4.8);
            createVirtualTutor("tutor040", "Bùi Thị Anh", "anh.bui@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor041", "Ngô Văn Bình", "binh.ngo@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.5);
            createVirtualTutor("tutor042", "Đỗ Thị Châu", "chau.do@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor043", "Trương Văn Dũng", "dung.truong@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 6, 5, 4.9);
            createVirtualTutor("tutor044", "Nguyễn Thị Giang", "giang.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7);
            createVirtualTutor("tutor045", "Phan Văn Hùng", "hung.phan@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor046", "Lê Thị Khánh", "khanh.le@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.8);
            createVirtualTutor("tutor047", "Trần Văn Lâm", "lam.tran@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor048", "Hoàng Thị Mỹ", "my.hoang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.5);
            createVirtualTutor("tutor049", "Nguyễn Văn Nghĩa", "nghia.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7);
            createVirtualTutor("tutor050", "Phạm Thị Oanh", "oanh.pham@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.6);

            // Electronic Engineering Department (20 tutors)
            createVirtualTutor("tutor051", "Lê Văn Phúc", "phuc.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor052", "Trần Thị Quý", "quy.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor053", "Nguyễn Văn Sơn", "son.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6);
            createVirtualTutor("tutor054", "Võ Thị Thu", "thu.vo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.8);
            createVirtualTutor("tutor055", "Đặng Văn Tuấn", "tuan.dang@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9);
            createVirtualTutor("tutor056", "Bùi Thị Vân", "van.bui@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.5);
            createVirtualTutor("tutor057", "Ngô Văn Xuân", "xuan.ngo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.7);
            createVirtualTutor("tutor058", "Đỗ Thị Yến", "yen.do@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6);
            createVirtualTutor("tutor059", "Trương Văn An", "an.truong@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 6, 4, 4.8);
            createVirtualTutor("tutor060", "Nguyễn Thị Bích", "bich.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.7);
            createVirtualTutor("tutor061", "Phan Văn Cường", "cuong.phan@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.5);
            createVirtualTutor("tutor062", "Lê Thị Duyên", "duyen.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor063", "Trần Văn Hiếu", "hieu.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9);
            createVirtualTutor("tutor064", "Hoàng Thị Kim", "kim.hoang@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7);
            createVirtualTutor("tutor065", "Nguyễn Văn Lộc", "loc.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor066", "Phạm Thị Minh", "minh.pham@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.8);
            createVirtualTutor("tutor067", "Lê Văn Nhật", "nhat.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor068", "Trần Thị Phương", "phuong.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.5);
            createVirtualTutor("tutor069", "Nguyễn Văn Quang", "quang.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.7);
            createVirtualTutor("tutor070", "Võ Thị Thanh", "thanh.vo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.6);

// Mechanical Engineering Department (15 tutors)
            createVirtualTutor("tutor071", "Đặng Văn Tiến", "tien.dang@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 6, 4, 4.8);
            createVirtualTutor("tutor072", "Bùi Thị Uyên", "uyen.bui@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.7);
            createVirtualTutor("tutor073", "Ngô Văn Vinh", "vinh.ngo@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.6);
            createVirtualTutor("tutor074", "Đỗ Thị Xuân", "xuan.do@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS), 5, 3, 4.5);
            createVirtualTutor("tutor075", "Trương Văn Yến", "yen.truong@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 6, 5, 4.9);
            createVirtualTutor("tutor076", "Nguyễn Thị Ánh", "anh.nguyen2@hcmut.edu.vn", Department.ME, // Email adjusted for potential uniqueness
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.7);
            createVirtualTutor("tutor077", "Phan Văn Bình", "binh.phan@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6);
            createVirtualTutor("tutor078", "Lê Thị Châu", "chau.le@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.8);
            createVirtualTutor("tutor079", "Trần Văn Duy", "duy.tran@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS, Expertise.MATERIALS_SCIENCE), 6, 4, 4.9);
            createVirtualTutor("tutor080", "Hoàng Thị Hà", "ha.hoang@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 5, 3, 4.5);
            createVirtualTutor("tutor081", "Nguyễn Văn Kiên", "kien.nguyen@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.7);
            createVirtualTutor("tutor082", "Phạm Thị Linh", "linh.pham@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6);
            createVirtualTutor("tutor083", "Lê Văn Mạnh", "manh.le@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 6, 5, 4.8);
            createVirtualTutor("tutor084", "Trần Thị Nga", "nga.tran@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS), 4, 2, 4.5);
            createVirtualTutor("tutor085", "Nguyễn Văn Phát", "phat.nguyen@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 5, 3, 4.7);

// Chemical Engineering Department (15 tutors)
            createVirtualTutor("tutor086", "Võ Thị Quỳnh", "quynh.vo2@hcmut.edu.vn", Department.CH, // Email adjusted
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 4, 4.8);
            createVirtualTutor("tutor087", "Đặng Văn Sang", "sang.dang@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.7);
            createVirtualTutor("tutor088", "Bùi Thị Thủy", "thuy.bui@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 4, 2, 4.6);
            createVirtualTutor("tutor089", "Ngô Văn Tùng", "tung.ngo@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.5);
            createVirtualTutor("tutor090", "Đỗ Thị Vân", "van.do2@hcmut.edu.vn", Department.CH, // Email adjusted
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 5, 4.9);
            createVirtualTutor("tutor091", "Trương Văn Xuân", "xuan.truong2@hcmut.edu.vn", Department.CH, // Email adjusted
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.7);
            createVirtualTutor("tutor092", "Nguyễn Thị Yến", "yen.nguyen@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6);
            createVirtualTutor("tutor093", "Phan Văn Anh", "anh.phan@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.8);
            createVirtualTutor("tutor094", "Lê Thị Bảo", "bao.le@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 4, 4.9);
            createVirtualTutor("tutor095", "Trần Văn Cường", "cuong.tran2@hcmut.edu.vn", Department.CH, // Email adjusted
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.5);
            createVirtualTutor("tutor096", "Hoàng Thị Diễm", "diem.hoang@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 4, 2, 4.7);
            createVirtualTutor("tutor097", "Nguyễn Văn Huy", "huy.nguyen@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.6);
            createVirtualTutor("tutor098", "Phạm Thị Kiều", "kieu.pham@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 5, 4.8);
            createVirtualTutor("tutor099", "Lê Văn Long", "long.le@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.5);
            createVirtualTutor("tutor100", "Trần Thị Mai", "mai.tran@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 5, 3, 4.7);

            System.out.println("Successfully created 100 virtual tutors!");
            System.out.println("Distribution: CS=30, CE=20, EE=20, ME=15, CH=15");

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

            //Generate avatarURL
            String avatarUrl = "https://i.pravatar.cc/150?u=" + tutorId;

            String[] nameParts = fullName.split(" ");
            String firstName = (nameParts.length > 0) ? nameParts[nameParts.length - 1] : "";
            String lastName = (nameParts.length > 1) ?
                    String.join(" ", Arrays.copyOfRange(nameParts, 0, nameParts.length - 1)) :
                    ""; // Hoặc gán bằng nameParts[0] nếu muốn có họ khi chỉ có 1 từ

            User tutorUser = new User(
                    tutorId,
                    firstName, // Tên
                    lastName,  // Họ và tên đệm
                    department,
                    List.of(Role.TUTOR),
                    username,
                    avatarUrl,
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
                "student001", "Trần Thị Lan", "lan.tran@hcmut.edu.vn", Department.CS,
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

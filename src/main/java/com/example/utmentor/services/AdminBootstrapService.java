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

            // Computer Science Department (30 tutors)
            createVirtualTutor("tutor001", "Nguyen Van An", "an.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.MACHINE_LEARNING, Expertise.DATA_SCIENCE), 5, 3, 4.8);
            createVirtualTutor("tutor002", "Tran Thi Binh", "binh.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN), 4, 2, 4.6);
            createVirtualTutor("tutor003", "Le Van Cuong", "cuong.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.DATA_SCIENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 6, 4, 4.9);
            createVirtualTutor("tutor004", "Pham Thi Dung", "dung.pham@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS), 3, 1, 4.7);
            createVirtualTutor("tutor005", "Hoang Van Em", "em.hoang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.5);
            createVirtualTutor("tutor006", "Vo Thi Phuong", "phuong.vo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 5, 3, 4.7);
            createVirtualTutor("tutor007", "Dang Van Quang", "quang.dang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MACHINE_LEARNING, Expertise.DATA_SCIENCE, Expertise.ALGORITHMS), 6, 4, 4.8);
            createVirtualTutor("tutor008", "Bui Thi Hoa", "hoa.bui@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE), 5, 2, 4.6);
            createVirtualTutor("tutor009", "Ngo Van Inh", "inh.ngo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATABASE_DESIGN, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 3, 4.5);
            createVirtualTutor("tutor010", "Do Thi Kim", "kim.do@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 5, 2, 4.7);
            createVirtualTutor("tutor011", "Truong Van Long", "long.truong@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS), 6, 5, 4.9);
            createVirtualTutor("tutor012", "Nguyen Thi Mai", "mai.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.6);
            createVirtualTutor("tutor013", "Phan Van Nam", "nam.phan@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.MOBILE_DEVELOPMENT, Expertise.DATABASE_DESIGN), 5, 3, 4.7);
            createVirtualTutor("tutor014", "Le Thi Oanh", "oanh.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATA_SCIENCE, Expertise.MACHINE_LEARNING), 4, 2, 4.5);
            createVirtualTutor("tutor015", "Tran Van Phuc", "phuc.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ALGORITHMS), 6, 4, 4.8);
            createVirtualTutor("tutor016", "Hoang Thi Quynh", "quynh.hoang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.DATABASE_DESIGN), 5, 3, 4.6);
            createVirtualTutor("tutor017", "Nguyen Van Sang", "sang.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.ALGORITHMS, Expertise.COMPUTER_NETWORKS), 4, 2, 4.7);
            createVirtualTutor("tutor018", "Pham Thi Thao", "thao.pham@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT), 5, 2, 4.5);
            createVirtualTutor("tutor019", "Le Van Uyen", "uyen.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MACHINE_LEARNING, Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 6, 5, 4.9);
            createVirtualTutor("tutor020", "Tran Thi Van", "van.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 4, 3, 4.6);
            createVirtualTutor("tutor021", "Nguyen Van Xuan", "xuan.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING, Expertise.ALGORITHMS), 5, 2, 4.7);
            createVirtualTutor("tutor022", "Vo Thi Yen", "yen.vo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.MACHINE_LEARNING), 6, 4, 4.8);
            createVirtualTutor("tutor023", "Dang Van An", "an.dang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.5);
            createVirtualTutor("tutor024", "Bui Thi Bao", "bao.bui@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATA_SCIENCE, Expertise.ALGORITHMS), 5, 3, 4.6);
            createVirtualTutor("tutor025", "Ngo Van Cao", "cao.ngo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.7);
            createVirtualTutor("tutor026", "Do Thi Dao", "dao.do@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 5, 3, 4.5);
            createVirtualTutor("tutor027", "Truong Van Hai", "hai.truong@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MACHINE_LEARNING, Expertise.DATA_SCIENCE), 6, 5, 4.9);
            createVirtualTutor("tutor028", "Nguyen Thi Hue", "hue.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.ARTIFICIAL_INTELLIGENCE), 4, 2, 4.6);
            createVirtualTutor("tutor029", "Phan Van Khoa", "khoa.phan@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS), 5, 3, 4.8);
            createVirtualTutor("tutor030", "Le Thi Lan", "lan.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.WEB_DEVELOPMENT), 4, 2, 4.5);

            // Computer Engineering Department (20 tutors)
            createVirtualTutor("tutor031", "Tran Van Minh", "minh.tran@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor032", "Hoang Thi Nga", "nga.hoang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor033", "Nguyen Van Phong", "phong.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6);
            createVirtualTutor("tutor034", "Pham Thi Quynh", "quynh.pham@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.8);
            createVirtualTutor("tutor035", "Le Van Tam", "tam.le@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9);
            createVirtualTutor("tutor036", "Tran Thi Uyen", "uyen.tran@hcmut.edu.vn", Department.CE,
                    List.of( Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.5);
            createVirtualTutor("tutor037", "Nguyen Van Vinh", "vinh.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor038", "Vo Thi Xuan", "xuan.vo@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.6);
            createVirtualTutor("tutor039", "Dang Van Yen", "yen.dang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS), 6, 4, 4.8);
            createVirtualTutor("tutor040", "Bui Thi Anh", "anh.bui@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor041", "Ngo Van Binh", "binh.ngo@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.5);
            createVirtualTutor("tutor042", "Do Thi Chau", "chau.do@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor043", "Truong Van Dung", "dung.truong@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 6, 5, 4.9);
            createVirtualTutor("tutor044", "Nguyen Thi Giang", "giang.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7);
            createVirtualTutor("tutor045", "Phan Van Hung", "hung.phan@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor046", "Le Thi Khanh", "khanh.le@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.8);
            createVirtualTutor("tutor047", "Tran Van Lam", "lam.tran@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor048", "Hoang Thi My", "my.hoang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.5);
            createVirtualTutor("tutor049", "Nguyen Van Nghia", "nghia.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7);
            createVirtualTutor("tutor050", "Pham Thi Oanh", "oanh.pham@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.6);

            // Electronic Engineering Department (20 tutors)
            createVirtualTutor("tutor051", "Le Van Phuc", "phuc.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor052", "Tran Thi Quy", "quy.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7);
            createVirtualTutor("tutor053", "Nguyen Van Son", "son.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6);
            createVirtualTutor("tutor054", "Vo Thi Thu", "thu.vo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.8);
            createVirtualTutor("tutor055", "Dang Van Tuan", "tuan.dang@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9);
            createVirtualTutor("tutor056", "Bui Thi Van", "van.bui@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.5);
            createVirtualTutor("tutor057", "Ngo Van Xuan", "xuan.ngo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.7);
            createVirtualTutor("tutor058", "Do Thi Yen", "yen.do@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6);
            createVirtualTutor("tutor059", "Truong Van An", "an.truong@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 6, 4, 4.8);
            createVirtualTutor("tutor060", "Nguyen Thi Bich", "bich.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.7);
            createVirtualTutor("tutor061", "Phan Van Cuong", "cuong.phan@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.5);
            createVirtualTutor("tutor062", "Le Thi Duyen", "duyen.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor063", "Tran Van Hieu", "hieu.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9);
            createVirtualTutor("tutor064", "Hoang Thi Kim", "kim.hoang@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7);
            createVirtualTutor("tutor065", "Nguyen Van Loc", "loc.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.6);
            createVirtualTutor("tutor066", "Pham Thi Minh", "minh.pham@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.8);
            createVirtualTutor("tutor067", "Le Van Nhat", "nhat.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 6, 4, 4.9);
            createVirtualTutor("tutor068", "Tran Thi Phuong", "phuong.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.5);
            createVirtualTutor("tutor069", "Nguyen Van Quang", "quang.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.7);
            createVirtualTutor("tutor070", "Vo Thi Thanh", "thanh.vo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.6);

            // Mechanical Engineering Department (15 tutors)
            createVirtualTutor("tutor071", "Dang Van Tien", "tien.dang@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 6, 4, 4.8);
            createVirtualTutor("tutor072", "Bui Thi Uyen", "uyen.bui@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.7);
            createVirtualTutor("tutor073", "Ngo Van Vinh", "vinh.ngo@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.6);
            createVirtualTutor("tutor074", "Do Thi Xuan", "xuan.do@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS), 5, 3, 4.5);
            createVirtualTutor("tutor075", "Truong Van Yen", "yen.truong@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 6, 5, 4.9);
            createVirtualTutor("tutor076", "Nguyen Thi Anh", "anh.nguyen2@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.7);
            createVirtualTutor("tutor077", "Phan Van Binh", "binh.phan@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6);
            createVirtualTutor("tutor078", "Le Thi Chau", "chau.le@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.8);
            createVirtualTutor("tutor079", "Tran Van Duy", "duy.tran@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS, Expertise.MATERIALS_SCIENCE), 6, 4, 4.9);
            createVirtualTutor("tutor080", "Hoang Thi Ha", "ha.hoang@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 5, 3, 4.5);
            createVirtualTutor("tutor081", "Nguyen Van Kien", "kien.nguyen@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.7);
            createVirtualTutor("tutor082", "Pham Thi Linh", "linh.pham@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6);
            createVirtualTutor("tutor083", "Le Van Manh", "manh.le@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 6, 5, 4.8);
            createVirtualTutor("tutor084", "Tran Thi Nga", "nga.tran@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS), 4, 2, 4.5);
            createVirtualTutor("tutor085", "Nguyen Van Phat", "phat.nguyen@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 5, 3, 4.7);

            // Chemical Engineering Department (15 tutors)
            createVirtualTutor("tutor086", "Vo Thi Quynh", "quynh.vo2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 4, 4.8);
            createVirtualTutor("tutor087", "Dang Van Sang", "sang.dang@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.7);
            createVirtualTutor("tutor088", "Bui Thi Thuy", "thuy.bui@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 4, 2, 4.6);
            createVirtualTutor("tutor089", "Ngo Van Tung", "tung.ngo@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.5);
            createVirtualTutor("tutor090", "Do Thi Van", "van.do2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 5, 4.9);
            createVirtualTutor("tutor091", "Truong Van Xuan", "xuan.truong2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.7);
            createVirtualTutor("tutor092", "Nguyen Thi Yen", "yen.nguyen@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6);
            createVirtualTutor("tutor093", "Phan Van Anh", "anh.phan@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.8);
            createVirtualTutor("tutor094", "Le Thi Bao", "bao.le@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 4, 4.9);
            createVirtualTutor("tutor095", "Tran Van Cuong", "cuong.tran2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.5);
            createVirtualTutor("tutor096", "Hoang Thi Diem", "diem.hoang@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 4, 2, 4.7);
            createVirtualTutor("tutor097", "Nguyen Van Huy", "huy.nguyen@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.6);
            createVirtualTutor("tutor098", "Pham Thi Kieu", "kieu.pham@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 5, 4.8);
            createVirtualTutor("tutor099", "Le Van Long", "long.le@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.5);
            createVirtualTutor("tutor100", "Tran Thi Mai", "mai.tran@hcmut.edu.vn", Department.CH,
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

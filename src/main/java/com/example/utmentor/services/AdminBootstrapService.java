package com.example.utmentor.services;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;

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
import com.example.utmentor.models.webModels.profile.Achievement;
import com.example.utmentor.models.webModels.profile.AchievementType;

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
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 5, 3, 4.8,
                    generatePhoneNumber("tutor001"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu về khoa học dữ liệu.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor002", "Trần Thị Bình", "binh.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN), 4, 2, 4.6,
                    generatePhoneNumber("tutor002"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu về phát triển web.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor003", "Lê Văn Cường", "cuong.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.DATA_SCIENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 6, 4, 4.9,
                    generatePhoneNumber("tutor003"),
                    "Chuyên gia giải thuật tập trung vào lập trình thi đấu và khoa học dữ liệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor004", "Phạm Thị Dung", "dung.pham@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS), 3, 1, 4.7,
                    generatePhoneNumber("tutor004"),
                    "Chuyên gia an ninh mạng với kỹ năng kiểm thử xâm nhập trong mạng máy tính.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor005", "Hoàng Văn Em", "em.hoang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.5,
                    generatePhoneNumber("tutor005"),
                    "Nhà phát triển ứng dụng di động cho iOS và Android có kinh nghiệm phát triển web.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor006", "Võ Thị Phương", "phuong.vo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 5, 3, 4.7,
                    generatePhoneNumber("tutor006"),
                    "Nhà phát triển web full-stack có chuyên môn về các framework hiện đại trong thiết kế cơ sở dữ liệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor007", "Đặng Văn Quang", "quang.dang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE, Expertise.ALGORITHMS), 6, 4, 4.8,
                    generatePhoneNumber("tutor007"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu về khoa học dữ liệu và giải thuật.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor008", "Bùi Thị Hòa", "hoa.bui@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE), 5, 2, 4.6,
                    generatePhoneNumber("tutor008"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor009", "Ngô Văn Ính", "inh.ngo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATABASE_DESIGN, Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 3, 4.5,
                    generatePhoneNumber("tutor009"),
                    "Kiến trúc sư cơ sở dữ liệu chuyên về tối ưu hóa và thiết kế cho phát triển web.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor010", "Đỗ Thị Kim", "kim.do@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 5, 2, 4.7,
                    generatePhoneNumber("tutor010"),
                    "Nhà phát triển ứng dụng di động cho iOS và Android, tập trung vào kỹ thuật phần mềm.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor011", "Trương Văn Long", "long.truong@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS), 6, 5, 4.9,
                    generatePhoneNumber("tutor011"),
                    "Chuyên gia an ninh mạng với kỹ năng kiểm thử xâm nhập trong mạng máy tính.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor012", "Nguyễn Thị Mai", "mai.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.6,
                    generatePhoneNumber("tutor012"),
                    "Chuyên gia giải thuật tập trung vào lập trình thi đấu và kỹ thuật phần mềm.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor013", "Phan Văn Nam", "nam.phan@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.MOBILE_DEVELOPMENT, Expertise.DATABASE_DESIGN), 5, 3, 4.7,
                    generatePhoneNumber("tutor013"),
                    "Nhà phát triển web full-stack có chuyên môn về các framework hiện đại trong phát triển di động.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor014", "Lê Thị Oanh", "oanh.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATA_SCIENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 4, 2, 4.5,
                    generatePhoneNumber("tutor014"),
                    "Nhà khoa học dữ liệu có kinh nghiệm lập mô hình thống kê trong trí tuệ nhân tạo.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor015", "Trần Văn Phúc", "phuc.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ALGORITHMS), 6, 4, 4.8,
                    generatePhoneNumber("tutor015"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu về giải thuật.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor016", "Hoàng Thị Quỳnh", "quynh.hoang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.DATABASE_DESIGN), 5, 3, 4.6,
                    generatePhoneNumber("tutor016"),
                    "Kỹ sư phần mềm có kinh nghiệm phát triển Agile trong thiết kế cơ sở dữ liệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor017", "Nguyễn Văn Sang", "sang.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.ALGORITHMS, Expertise.COMPUTER_NETWORKS), 4, 2, 4.7,
                    generatePhoneNumber("tutor017"),
                    "Chuyên gia an ninh mạng với kỹ năng kiểm thử xâm nhập trong lĩnh vực giải thuật.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor018", "Phạm Thị Thảo", "thao.pham@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.WEB_DEVELOPMENT), 5, 2, 4.5,
                    generatePhoneNumber("tutor018"),
                    "Nhà phát triển ứng dụng di động cho iOS và Android có kinh nghiệm phát triển web.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor019", "Lê Văn Uyên", "uyen.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 6, 5, 4.9,
                    generatePhoneNumber("tutor019"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu các ứng dụng khoa học dữ liệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor020", "Trần Thị Vân", "van.tran@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 4, 3, 4.6,
                    generatePhoneNumber("tutor020"),
                    "Kiến trúc sư cơ sở dữ liệu chuyên về tối ưu hóa và thiết kế cho kỹ thuật phần mềm.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor021", "Nguyễn Văn Xuân", "xuan.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING, Expertise.ALGORITHMS), 5, 2, 4.7,
                    generatePhoneNumber("tutor021"),
                    "Nhà phát triển web full-stack có chuyên môn về các framework hiện đại trong kỹ thuật phần mềm.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor022", "Võ Thị Yến", "yen.vo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ARTIFICIAL_INTELLIGENCE), 6, 4, 4.8,
                    generatePhoneNumber("tutor022"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor023", "Đặng Văn An", "an.dang@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.5,
                    generatePhoneNumber("tutor023"),
                    "Chuyên gia an ninh mạng với kỹ năng kiểm thử xâm nhập trong kỹ thuật phần mềm.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor024", "Bùi Thị Bảo", "bao.bui@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.DATA_SCIENCE, Expertise.ALGORITHMS), 5, 3, 4.6,
                    generatePhoneNumber("tutor024"),
                    "Nhà khoa học dữ liệu có kinh nghiệm lập mô hình thống kê trong giải thuật.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor025", "Ngô Văn Cao", "cao.ngo@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.MOBILE_DEVELOPMENT, Expertise.SOFTWARE_ENGINEERING), 4, 2, 4.7,
                    generatePhoneNumber("tutor025"),
                    "Nhà phát triển ứng dụng di động cho iOS và Android có kinh nghiệm kỹ thuật phần mềm.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor026", "Đỗ Thị Đào", "dao.do@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.WEB_DEVELOPMENT, Expertise.DATABASE_DESIGN, Expertise.SOFTWARE_ENGINEERING), 5, 3, 4.5,
                    generatePhoneNumber("tutor026"),
                    "Nhà phát triển web full-stack có chuyên môn về các framework hiện đại trong thiết kế cơ sở dữ liệu.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor027", "Trương Văn Hải", "hai.truong@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.DATA_SCIENCE), 6, 5, 4.9,
                    generatePhoneNumber("tutor027"),
                    "Chuyên gia AI và học máy có kinh nghiệm nghiên cứu về khoa học dữ liệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor028", "Nguyễn Thị Huệ", "hue.nguyen@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.ALGORITHMS, Expertise.ARTIFICIAL_INTELLIGENCE), 4, 2, 4.6,
                    generatePhoneNumber("tutor028"),
                    "Chuyên gia giải thuật tập trung vào lập trình thi đấu có ứng dụng AI.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor029", "Phan Văn Khoa", "khoa.phan@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.CYBERSECURITY, Expertise.COMPUTER_NETWORKS, Expertise.ALGORITHMS), 5, 3, 4.8,
                    generatePhoneNumber("tutor029"),
                    "Chuyên gia an ninh mạng với kỹ năng kiểm thử xâm nhập trong mạng máy tính.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor030", "Lê Thị Lan", "lan.le@hcmut.edu.vn", Department.CS,
                    List.of(Expertise.SOFTWARE_ENGINEERING, Expertise.WEB_DEVELOPMENT), 4, 2, 4.5,
                    generatePhoneNumber("tutor030"),
                    "Kỹ sư phần mềm có kinh nghiệm phát triển Agile trong phát triển web.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));

            // Computer Engineering Department (20 tutors)
            createVirtualTutor("tutor031", "Trần Văn Minh", "minh.tran@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 6, 4, 4.9,
                    generatePhoneNumber("tutor031"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor032", "Hoàng Thị Nga", "nga.hoang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.7,
                    generatePhoneNumber("tutor032"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor033", "Nguyễn Văn Phong", "phong.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6,
                    generatePhoneNumber("tutor033"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor034", "Phạm Thị Quỳnh", "quynh.pham@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.8,
                    generatePhoneNumber("tutor034"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor035", "Lê Văn Tâm", "tam.le@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9,
                    generatePhoneNumber("tutor035"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor036", "Trần Thị Uyên", "uyen.tran@hcmut.edu.vn", Department.CE,
                    List.of( Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.5,
                    generatePhoneNumber("tutor036"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor037", "Nguyễn Văn Vinh", "vinh.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7,
                    generatePhoneNumber("tutor037"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor038", "Võ Thị Xuân", "xuan.vo@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.6,
                    generatePhoneNumber("tutor038"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor039", "Đặng Văn Yến", "yen.dang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS), 6, 4, 4.8,
                    generatePhoneNumber("tutor039"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn về tự động hóa.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor040", "Bùi Thị Anh", "anh.bui@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7,
                    generatePhoneNumber("tutor040"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu và hệ thống điều khiển.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor041", "Ngô Văn Bình", "binh.ngo@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 4, 2, 4.5,
                    generatePhoneNumber("tutor041"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor042", "Đỗ Thị Châu", "chau.do@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.6,
                    generatePhoneNumber("tutor042"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor043", "Trương Văn Dũng", "dung.truong@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 6, 5, 4.9,
                    generatePhoneNumber("tutor043"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor044", "Nguyễn Thị Giang", "giang.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7,
                    generatePhoneNumber("tutor044"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor045", "Phan Văn Hùng", "hung.phan@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 5, 3, 4.6,
                    generatePhoneNumber("tutor045"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong hệ thống điều khiển.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor046", "Lê Thị Khánh", "khanh.le@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.8,
                    generatePhoneNumber("tutor046"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor047", "Trần Văn Lâm", "lam.tran@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 6, 4, 4.9,
                    generatePhoneNumber("tutor047"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển và nhúng.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor048", "Hoàng Thị Mỹ", "my.hoang@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.5,
                    generatePhoneNumber("tutor048"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor049", "Nguyễn Văn Nghĩa", "nghia.nguyen@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7,
                    generatePhoneNumber("tutor049"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor050", "Phạm Thị Oanh", "oanh.pham@hcmut.edu.vn", Department.CE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.6,
                    generatePhoneNumber("tutor050"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));

            // Electronic Engineering Department (20 tutors)
            createVirtualTutor("tutor051", "Lê Văn Phúc", "phuc.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 4, 4.9,
                    generatePhoneNumber("tutor051"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor052", "Trần Thị Quý", "quy.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 5, 3, 4.7,
                    generatePhoneNumber("tutor052"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor053", "Nguyễn Văn Sơn", "son.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6,
                    generatePhoneNumber("tutor053"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong hệ thống điều khiển.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor054", "Võ Thị Thu", "thu.vo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.8,
                    generatePhoneNumber("tutor054"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống nhúng.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor055", "Đặng Văn Tuấn", "tuan.dang@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9,
                    generatePhoneNumber("tutor055"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu và hệ thống nhúng.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor056", "Bùi Thị Vân", "van.bui@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.5,
                    generatePhoneNumber("tutor056"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor057", "Ngô Văn Xuân", "xuan.ngo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.7,
                    generatePhoneNumber("tutor057"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor058", "Đỗ Thị Yến", "yen.do@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.6,
                    generatePhoneNumber("tutor058"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong hệ thống điều khiển.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor059", "Trương Văn An", "an.truong@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 6, 4, 4.8,
                    generatePhoneNumber("tutor059"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống nhúng.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor060", "Nguyễn Thị Bích", "bich.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.7,
                    generatePhoneNumber("tutor060"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor061", "Phan Văn Cường", "cuong.phan@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.CONTROL_SYSTEMS), 4, 2, 4.5,
                    generatePhoneNumber("tutor061"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong hệ thống điều khiển.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor062", "Lê Thị Duyên", "duyen.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.6,
                    generatePhoneNumber("tutor062"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống nhúng.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor063", "Trần Văn Hiếu", "hieu.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.EMBEDDED_SYSTEMS), 6, 5, 4.9,
                    generatePhoneNumber("tutor063"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong xử lý tín hiệu và hệ thống nhúng.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor064", "Hoàng Thị Kim", "kim.hoang@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 4, 2, 4.7,
                    generatePhoneNumber("tutor064"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor065", "Nguyễn Văn Lộc", "loc.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.6,
                    generatePhoneNumber("tutor065"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong hệ thống nhúng.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor066", "Phạm Thị Minh", "minh.pham@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.8,
                    generatePhoneNumber("tutor066"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor067", "Lê Văn Nhật", "nhat.le@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 6, 4, 4.9,
                    generatePhoneNumber("tutor067"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu và điều khiển.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor068", "Trần Thị Phương", "phuong.tran@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.CONTROL_SYSTEMS, Expertise.EMBEDDED_SYSTEMS), 5, 3, 4.5,
                    generatePhoneNumber("tutor068"),
                    "Kỹ sư hệ thống điều khiển có chuyên môn tự động hóa trong hệ thống nhúng.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor069", "Nguyễn Văn Quang", "quang.nguyen@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.SIGNAL_PROCESSING, Expertise.CONTROL_SYSTEMS), 4, 2, 4.7,
                    generatePhoneNumber("tutor069"),
                    "Chuyên gia xử lý tín hiệu trong truyền thông số có kinh nghiệm về hệ thống điều khiển.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor070", "Võ Thị Thanh", "thanh.vo@hcmut.edu.vn", Department.EE,
                    List.of(Expertise.EMBEDDED_SYSTEMS, Expertise.SIGNAL_PROCESSING), 5, 3, 4.6,
                    generatePhoneNumber("tutor070"),
                    "Kỹ sư hệ thống nhúng có kinh nghiệm IoT trong xử lý tín hiệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));

            // Mechanical Engineering Department (15 tutors)
            createVirtualTutor("tutor071", "Đặng Văn Tiến", "tien.dang@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 6, 4, 4.8,
                    generatePhoneNumber("tutor071"),
                    "Chuyên gia nhiệt động lực học trong hệ thống năng lượng có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor072", "Bùi Thị Uyên", "uyen.bui@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.7,
                    generatePhoneNumber("tutor072"),
                    "Nhà thiết kế cơ khí có trình độ CAD/CAM trong khoa học vật liệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor073", "Ngô Văn Vinh", "vinh.ngo@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.6,
                    generatePhoneNumber("tutor073"),
                    "Chuyên gia nhiệt động lực học trong hệ thống năng lượng có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor074", "Đỗ Thị Xuân", "xuan.do@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS), 5, 3, 4.5,
                    generatePhoneNumber("tutor074"),
                    "Nhà thiết kế cơ khí có trình độ CAD/CAM trong nhiệt động lực học.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor075", "Trương Văn Yến", "yen.truong@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 6, 5, 4.9,
                    generatePhoneNumber("tutor075"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor076", "Nguyễn Thị Ánh", "anh.nguyen2@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.7,
                    generatePhoneNumber("tutor076"),
                    "Chuyên gia nhiệt động lực học trong hệ thống năng lượng có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor077", "Phan Văn Bình", "binh.phan@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6,
                    generatePhoneNumber("tutor077"),
                    "Nhà thiết kế cơ khí có trình độ CAD/CAM trong khoa học vật liệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor078", "Lê Thị Châu", "chau.le@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.8,
                    generatePhoneNumber("tutor078"),
                    "Chuyên gia nhiệt động lực học trong hệ thống năng lượng có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor079", "Trần Văn Duy", "duy.tran@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS, Expertise.MATERIALS_SCIENCE), 6, 4, 4.9,
                    generatePhoneNumber("tutor079"),
                    "Nhà thiết kế cơ khí có trình độ CAD/CAM trong nhiệt động lực học và vật liệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor080", "Hoàng Thị Hà", "ha.hoang@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 5, 3, 4.5,
                    generatePhoneNumber("tutor080"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor081", "Nguyễn Văn Kiên", "kien.nguyen@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 4, 2, 4.7,
                    generatePhoneNumber("tutor081"),
                    "Chuyên gia nhiệt động lực học trong hệ thống năng lượng có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor082", "Phạm Thị Linh", "linh.pham@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6,
                    generatePhoneNumber("tutor082"),
                    "Nhà thiết kế cơ khí có trình độ CAD/CAM trong khoa học vật liệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor083", "Lê Văn Mạnh", "manh.le@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.THERMODYNAMICS, Expertise.MECHANICAL_DESIGN), 6, 5, 4.8,
                    generatePhoneNumber("tutor083"),
                    "Chuyên gia nhiệt động lực học trong hệ thống năng lượng có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor084", "Trần Thị Nga", "nga.tran@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MECHANICAL_DESIGN, Expertise.THERMODYNAMICS), 4, 2, 4.5,
                    generatePhoneNumber("tutor084"),
                    "Nhà thiết kế cơ khí có trình độ CAD/CAM trong nhiệt động lực học.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor085", "Nguyễn Văn Phát", "phat.nguyen@hcmut.edu.vn", Department.ME,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.MECHANICAL_DESIGN), 5, 3, 4.7,
                    generatePhoneNumber("tutor085"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm thiết kế cơ khí.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));

            // Chemical Engineering Department (15 tutors)
            createVirtualTutor("tutor086", "Võ Thị Quỳnh", "quynh.vo2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 4, 4.8,
                    generatePhoneNumber("tutor086"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor087", "Đặng Văn Sang", "sang.dang@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.7,
                    generatePhoneNumber("tutor087"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor088", "Bùi Thị Thủy", "thuy.bui@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 4, 2, 4.6,
                    generatePhoneNumber("tutor088"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor089", "Ngô Văn Tùng", "tung.ngo@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.5,
                    generatePhoneNumber("tutor089"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor090", "Đỗ Thị Vân", "van.do2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 5, 4.9,
                    generatePhoneNumber("tutor090"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor091", "Trương Văn Xuân", "xuan.truong2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.7,
                    generatePhoneNumber("tutor091"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor092", "Nguyễn Thị Yến", "yen.nguyen@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 5, 3, 4.6,
                    generatePhoneNumber("tutor092"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor093", "Phan Văn Anh", "anh.phan@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.8,
                    generatePhoneNumber("tutor093"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor094", "Lê Thị Bảo", "bao.le@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 4, 4.9,
                    generatePhoneNumber("tutor094"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.9 * 1.5)),
                    Math.max(5, (int)(4.9 * 7)));
            createVirtualTutor("tutor095", "Trần Văn Cường", "cuong.tran2@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.5,
                    generatePhoneNumber("tutor095"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor096", "Hoàng Thị Diễm", "diem.hoang@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 4, 2, 4.7,
                    generatePhoneNumber("tutor096"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));
            createVirtualTutor("tutor097", "Nguyễn Văn Huy", "huy.nguyen@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 5, 3, 4.6,
                    generatePhoneNumber("tutor097"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.6 * 1.5)),
                    Math.max(5, (int)(4.6 * 7)));
            createVirtualTutor("tutor098", "Phạm Thị Kiều", "kieu.pham@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 6, 5, 4.8,
                    generatePhoneNumber("tutor098"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.8 * 1.5)),
                    Math.max(5, (int)(4.8 * 7)));
            createVirtualTutor("tutor099", "Lê Văn Long", "long.le@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.MATERIALS_SCIENCE, Expertise.PROCESS_ENGINEERING), 4, 2, 4.5,
                    generatePhoneNumber("tutor099"),
                    "Nhà khoa học vật liệu nghiên cứu vật liệu tiên tiến có kinh nghiệm về kỹ thuật quy trình.",
                    Math.max(1, (int)(4.5 * 1.5)),
                    Math.max(5, (int)(4.5 * 7)));
            createVirtualTutor("tutor100", "Trần Thị Mai", "mai.tran@hcmut.edu.vn", Department.CH,
                    List.of(Expertise.PROCESS_ENGINEERING, Expertise.MATERIALS_SCIENCE), 5, 3, 4.7,
                    generatePhoneNumber("tutor100"),
                    "Kỹ sư quy trình tối ưu hóa quy trình công nghiệp có kinh nghiệm về khoa học vật liệu.",
                    Math.max(1, (int)(4.7 * 1.5)),
                    Math.max(5, (int)(4.7 * 7)));

            System.out.println("Successfully created 100 virtual tutors!");
            System.out.println("Distribution: CS=30, CE=20, EE=20, ME=15, CH=15");

        } catch (Exception e) {
            System.err.println("Error creating virtual tutors: " + e.getMessage());
            // Don't throw exception to avoid breaking admin creation
        }
    }
    private void createVirtualTutor(String tutorId, String fullName, String email,
                                    Department department, List<Expertise> expertise,
                                    int maxCapacity, int currentMentees, double rating,
                                    String phoneNumber, String tutorDescription,
                                    int yearsOfExperience, int totalStudentTaught) {
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

            tutorUser.setPhoneNumber(phoneNumber);

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

            tutorProfile.setTutorDescription(tutorDescription);
            tutorProfile.setYearsOfExperience(yearsOfExperience);
            tutorProfile.setTotalStudentTaught(totalStudentTaught);
            tutorProfile.setAchievements(createTutorAchievements(yearsOfExperience));

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
                    "20123456",
                    generatePhoneNumber("student001"),
                    3.75,
                    "I am passionate about machine learning and want to improve my data structures skills.",
                    List.of("Get IELTS 7.0 by end of year", "Master machine learning fundamentals", "Build a full-stack web application"),
                    List.of(Expertise.ARTIFICIAL_INTELLIGENCE, Expertise.ALGORITHMS, Expertise.DATA_SCIENCE)
            );

            System.out.println("Virtual student created successfully!");

        } catch (Exception e) {
            System.err.println("Error creating virtual student: " + e.getMessage());
            // Don't throw exception to avoid breaking admin creation
        }
    }

    private void createVirtualStudent(String studentId, String fullName, String email,
                                      Department department, String studentID,
                                      String phoneNumber, double currentGPA, String studentDescription,
                                      List<String> learningGoal, List<Expertise> demandCourse) {
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

            studentUser.setPhoneNumber(phoneNumber);

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

            studentProfile.setCurrentGPA(currentGPA);
            studentProfile.setStudentDescription(studentDescription);
            studentProfile.setLearningGoal(learningGoal);
            studentProfile.setDemandCourse(demandCourse);
            studentProfile.setAchievements(createStudentAchievements(currentGPA));

            studentProfileRepository.save(studentProfile);

            System.out.println("Created virtual student: " + fullName + " (" + email + ") - Student ID: " + studentID);

        } catch (Exception e) {
            System.err.println("Error creating student " + studentId + ": " + e.getMessage());
        }
    }

    private String generatePhoneNumber(String id) {
        int hash = id.hashCode();
        long phoneNum = 900000000L + (Math.abs(hash) % 100000000L);
        return "0" + phoneNum;
    }

    private List<Achievement> createTutorAchievements(int yearsOfExperience) {
        List<Achievement> achievements = new ArrayList<>();

        if (yearsOfExperience >= 5) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Giải thưởng Giảng dạy Xuất sắc", // Tiêu đề
                    "Được công nhận vì sự xuất sắc trong giảng dạy và cố vấn sinh viên trong hơn 5 năm.", // Mô tả
                    "2023",
                    AchievementType.AWARD
            ));
        }

        if (yearsOfExperience >= 3) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Chứng chỉ Kỹ thuật Phần mềm Nâng cao", // Tiêu đề
                    "Hoàn thành chứng chỉ chuyên nghiệp về các phương pháp phát triển phần mềm hiện đại.", // Mô tả
                    "2022",
                    AchievementType.CERTIFICATION
            ));
        }

        if (yearsOfExperience >= 2) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Giải thưởng Cố vấn Tốt nhất", // Tiêu đề
                    "Được trao tặng vì sự cố vấn đặc biệt và tỷ lệ sinh viên thành công cao.", // Mô tả
                    "2024",
                    AchievementType.AWARD
            ));
        }

        // Luôn thêm ít nhất một
        if (achievements.isEmpty()) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Chứng chỉ Trợ giảng", // Tiêu đề
                    "Trợ giảng được chứng nhận cho các khóa học đại học.", // Mô tả
                    "2025",
                    AchievementType.CERTIFICATION
            ));
        }

        return achievements;
    }

    private List<Achievement> createStudentAchievements(double gpa) {
        List<Achievement> achievements = new ArrayList<>();

        if (gpa >= 3.7) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Danh sách Khen thưởng của Khoa (Dean's List)", // Tiêu đề
                    "Được công nhận vì thành tích học tập xuất sắc với GPA ≥ 3.7.", // Mô tả
                    "2025",
                    AchievementType.AWARD
            ));
        }

        if (gpa >= 3.5) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Chuyên ngành Cấu trúc Dữ liệu & Giải thuật", // Tiêu đề
                    "Hoàn thành xuất sắc khóa học CTDL & GT nâng cao.", // Mô tả
                    "2024",
                    AchievementType.CERTIFICATION
            ));
        }

        if (gpa >= 3.0) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Cuộc thi Lập trình - Top 10", // Tiêu đề
                    "Đạt vị trí top 10 trong cuộc thi lập trình cấp trường.", // Mô tả
                    "2024",
                    AchievementType.AWARD
            ));
        }

        // Luôn thêm ít nhất một
        if (achievements.isEmpty()) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Giới thiệu về Khoa học Máy tính", // Tiêu đề
                    "Hoàn thành thành công khóa học CS cơ bản.", // Mô tả
                    "2025",
                    AchievementType.CERTIFICATION
            ));
        }

        return achievements;
    }
}
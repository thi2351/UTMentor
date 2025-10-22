package com.example.utmentor.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.infrastructures.repository.Interface.StudentProfileRepository;
import com.example.utmentor.util.ValidatorException;

@Service
public class StudentProfileService {
    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileService(StudentProfileRepository studentProfileRepository) {
        this.studentProfileRepository = studentProfileRepository;
    }

    public StudentProfile createStudentProfile(String userId, String studentID) {
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setId(userId);
        studentProfile.setStudentID(studentID);
        studentProfile.setActive(true);
        studentProfile.setCreatedAt(Instant.now());
        studentProfile.setUpdatedAt(Instant.now());
        
        return studentProfileRepository.save(studentProfile);
    }

    public Optional<StudentProfile> findById(String id) {
        return studentProfileRepository.findById(id);
    }

    public Optional<StudentProfile> findByStudentID(String studentID) {
        return studentProfileRepository.findByStudentID(studentID);
    }

    public List<StudentProfile> findAllActive() {
        return studentProfileRepository.findByIsActiveTrue();
    }

    public StudentProfile updateStudentProfile(String id, String studentID) {
        Optional<StudentProfile> existingProfile = studentProfileRepository.findById(id);
        if (existingProfile.isPresent()) {
            StudentProfile profile = existingProfile.get();
            profile.setStudentID(studentID);
            return studentProfileRepository.save(profile);
        }
        ValidatorException ex = new ValidatorException("Student Profile Not Found");
        ex.setHttpCode(HttpStatus.NOT_FOUND);
        ex.add("id", "not_found", "Student profile not found");
        throw ex;
    }

    public void deactivateStudentProfile(String id) {
        Optional<StudentProfile> existingProfile = studentProfileRepository.findById(id);
        if (existingProfile.isPresent()) {
            StudentProfile profile = existingProfile.get();
            profile.setActive(false);
            studentProfileRepository.save(profile);
        }
    }

    public void deleteStudentProfile(String id) {
        studentProfileRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return studentProfileRepository.existsById(id);
    }

    public boolean existsByStudentID(String studentID) {
        return studentProfileRepository.existsByStudentID(studentID);
    }
}

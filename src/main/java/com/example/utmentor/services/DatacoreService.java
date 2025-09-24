package com.example.utmentor.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.DatacoreRepository;
import com.example.utmentor.models.docEntities.HCMUT_DATACORE.Datacore;
import com.example.utmentor.models.webModels.datacore.CreateDatacoreRequest;
import com.example.utmentor.models.webModels.datacore.DatacoreResponse;
import com.example.utmentor.models.webModels.datacore.UpdateDatacoreRequest;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

@Service
public class DatacoreService {
    private final DatacoreRepository _repository;
    
    public DatacoreService(DatacoreRepository repository) {
        this._repository = repository;
    }

    public DatacoreResponse createDatacore(CreateDatacoreRequest request) {
        ValidatorException ex = new ValidatorException("Create datacore request failed.");

        // Check if email already exists
        if (_repository.existsByStudentEmail(request.studentEmail())) {
            ex.add(Errors.DATACORE_EMAIL_EXISTS);
        }

        if (ex.hasAny()) {
            ex.setHttpCode(HttpStatus.BAD_REQUEST);
            throw ex;
        }

        // Create new datacore record
        String id = UUID.randomUUID().toString();
        Datacore datacore = new Datacore(
                id,
                request.firstName(),
                request.lastName(),
                request.department(),
                request.role(),
                request.studentEmail(),
                request.studentProfile(),
                request.tutorProfile()
        );

        Datacore savedDatacore = _repository.save(datacore);
        return mapToResponse(savedDatacore);
    }

    public DatacoreResponse getDatacoreById(String id) {
        Optional<Datacore> datacoreOpt = _repository.findById(id);
        if (datacoreOpt.isEmpty()) {
            ValidatorException ex = new ValidatorException("Datacore not found.");
            ex.add(Errors.DATACORE_NOT_FOUND);
            ex.setHttpCode(HttpStatus.NOT_FOUND);
            throw ex;
        }
        return mapToResponse(datacoreOpt.get());
    }

    public DatacoreResponse getDatacoreByEmail(String email) {
        Optional<Datacore> datacoreOpt = _repository.findByStudentEmail(email);
        if (datacoreOpt.isEmpty()) {
            ValidatorException ex = new ValidatorException("Datacore not found.");
            ex.add(Errors.DATACORE_NOT_FOUND);
            ex.setHttpCode(HttpStatus.NOT_FOUND);
            throw ex;
        }
        return mapToResponse(datacoreOpt.get());
    }

    public List<DatacoreResponse> getAllDatacore() {
        List<Datacore> datacores = _repository.findAll();
        return datacores.stream()
                .filter(datacore -> !datacore.isDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DatacoreResponse updateDatacore(UpdateDatacoreRequest request) {
        ValidatorException ex = new ValidatorException("Update datacore request failed.");

        // Check if datacore exists
        Optional<Datacore> existingOpt = _repository.findById(request.id());
        if (existingOpt.isEmpty()) {
            ex.add(Errors.DATACORE_NOT_FOUND);
            ex.setHttpCode(HttpStatus.NOT_FOUND);
            throw ex;
        }

        Datacore existing = existingOpt.get();
        if (existing.isDeleted()) {
            ex.add(Errors.DATACORE_ALREADY_DELETED);
            ex.setHttpCode(HttpStatus.BAD_REQUEST);
            throw ex;
        }

        // Check if email is being changed and if new email already exists
        if (!existing.getStudentEmail().equals(request.studentEmail()) && 
            _repository.existsByStudentEmail(request.studentEmail())) {
            ex.add(Errors.DATACORE_EMAIL_EXISTS);
        }

        if (ex.hasAny()) {
            ex.setHttpCode(HttpStatus.BAD_REQUEST);
            throw ex;
        }

        // Update fields
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setDepartment(request.department());
        existing.setRole(request.role());
        existing.setStudentEmail(request.studentEmail());
        existing.setStudentProfile(request.studentProfile());
        existing.setTutorProfile(request.tutorProfile());

        Datacore updatedDatacore = _repository.save(existing);
        return mapToResponse(updatedDatacore);
    }

    public void deleteDatacore(String id) {
        Optional<Datacore> datacoreOpt = _repository.findById(id);
        if (datacoreOpt.isEmpty()) {
            ValidatorException ex = new ValidatorException("Datacore not found.");
            ex.add(Errors.DATACORE_NOT_FOUND);
            ex.setHttpCode(HttpStatus.NOT_FOUND);
            throw ex;
        }

        Datacore datacore = datacoreOpt.get();
        if (datacore.isDeleted()) {
            ValidatorException ex = new ValidatorException("Datacore already deleted.");
            ex.add(Errors.DATACORE_ALREADY_DELETED);
            ex.setHttpCode(HttpStatus.BAD_REQUEST);
            throw ex;
        }

        // Soft delete
        datacore.setDeleted(true);
        _repository.save(datacore);
    }

    public void hardDeleteDatacore(String id) {
        if (!_repository.existsById(id)) {
            ValidatorException ex = new ValidatorException("Datacore not found.");
            ex.add(Errors.DATACORE_NOT_FOUND);
            ex.setHttpCode(HttpStatus.NOT_FOUND);
            throw ex;
        }
        _repository.deleteById(id);
    }

    private DatacoreResponse mapToResponse(Datacore datacore) {
        return new DatacoreResponse(
                datacore.getId(),
                datacore.getFirstName(),
                datacore.getLastName(),
                datacore.getDepartment(),
                datacore.getRole(),
                datacore.getStudentEmail(),
                datacore.getStudentProfile(),
                datacore.getTutorProfile(),
                datacore.isDeleted()
        );
    }
}

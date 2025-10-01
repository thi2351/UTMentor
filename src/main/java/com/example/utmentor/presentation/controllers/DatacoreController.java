package com.example.utmentor.presentation.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.webModels.datacore.CreateDatacoreRequest;
import com.example.utmentor.models.webModels.datacore.DatacoreResponse;
import com.example.utmentor.models.webModels.datacore.UpdateDatacoreRequest;
import com.example.utmentor.services.DatacoreService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/datacore")
public class DatacoreController {
    private final DatacoreService _service;

    public DatacoreController(DatacoreService service) {
        this._service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AFFAIR')")
    public ResponseEntity<DatacoreResponse> createDatacore(@Valid @RequestBody CreateDatacoreRequest request) {
        DatacoreResponse response = _service.createDatacore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AFFAIR')")
    public ResponseEntity<List<DatacoreResponse>> getAllDatacore() {
        List<DatacoreResponse> responses = _service.getAllDatacore();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AFFAIR')")
    public ResponseEntity<DatacoreResponse> getDatacoreById(@PathVariable String id) {
        DatacoreResponse response = _service.getDatacoreById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AFFAIR')")
    public ResponseEntity<DatacoreResponse> getDatacoreByEmail(@PathVariable String email) {
        DatacoreResponse response = _service.getDatacoreByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AFFAIR')")
    public ResponseEntity<DatacoreResponse> updateDatacore(@Valid @RequestBody UpdateDatacoreRequest request) {
        DatacoreResponse response = _service.updateDatacore(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDatacore(@PathVariable String id) {
        _service.deleteDatacore(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> hardDeleteDatacore(@PathVariable String id) {
        _service.hardDeleteDatacore(id);
        return ResponseEntity.noContent().build();
    }
}

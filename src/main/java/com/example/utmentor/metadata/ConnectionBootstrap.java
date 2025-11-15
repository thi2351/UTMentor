package com.example.utmentor.metadata;

import java.time.Instant;
import java.util.List;

import com.example.utmentor.models.docEntities.Connection.StatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.utmentor.infrastructures.repository.Interface.ConnectionRepository;
import com.example.utmentor.models.docEntities.Connection.Connection;

/**
 * Component responsible for creating bootstrap connections during initialization.
 * Contains all connection data and creation logic.
 */
@Component
public class ConnectionBootstrap {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private BootstrapHelper helper;

    /**
     * Data structure for a single connection entry
     */
    private static class ConnectionData {
        final String tutorId;
        final String studentId;
        final StatusRequest status;
        final String message;
        final String timestamp;

        ConnectionData(String tutorId, String studentId, StatusRequest status, String message, String timestamp) {
            this.tutorId = tutorId;
            this.studentId = studentId;
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    /**
     * Create all bootstrap connections.
     */
    public void createBootstrapConnections() {
        try {
            System.out.println("Creating bootstrap connections...");

            // Get connection data
            List<ConnectionData> tutor001Connections = getTutor001Connections();
            List<ConnectionData> tutor002Connections = getTutor002Connections();
            List<ConnectionData> tutor003Connections = getTutor003Connections();
            List<ConnectionData> tutor004Connections = getTutor004Connections();
            List<ConnectionData> tutor005Connections = getTutor005Connections();

            // Create connections for all tutors
            createConnectionsForTutor(tutor001Connections);
            createConnectionsForTutor(tutor002Connections);
            createConnectionsForTutor(tutor003Connections);
            createConnectionsForTutor(tutor004Connections);
            createConnectionsForTutor(tutor005Connections);

            System.out.println("Successfully created bootstrap connections!");
            System.out.println("  - tutor001: " + tutor001Connections.size() + " connections");
            System.out.println("  - tutor002: " + tutor002Connections.size() + " connections");
            System.out.println("  - tutor003: " + tutor003Connections.size() + " connections");
            System.out.println("  - tutor004: " + tutor004Connections.size() + " connections");
            System.out.println("  - tutor005: " + tutor005Connections.size() + " connections");

        } catch (Exception e) {
            System.err.println("Error creating bootstrap connections: " + e.getMessage());
        }
    }

    /**
     * Get all bootstrap connections for tutor001.
     * Using current timestamps relative to now
     */
    private List<ConnectionData> getTutor001Connections() {
        Instant now = Instant.now();
        return List.of(
                // Accepted connections (2)
                new ConnectionData("tutor001", "student001", StatusRequest.ACCEPTED, "I'd love to learn AI/ML from you. Looking forward to working together!", now.minusSeconds(20 * 24 * 3600).toString()),
                new ConnectionData("tutor001", "student002", StatusRequest.ACCEPTED, "Interested in deep learning mentorship. Can we discuss my project?", now.minusSeconds(15 * 24 * 3600).toString()),

                // Pending connection (1)
                new ConnectionData("tutor001", "student003", StatusRequest.PENDING, "Need guidance on neural networks for my thesis.", now.minusSeconds(2 * 24 * 3600).toString())
        );
    }

    /**
     * Get all bootstrap connections for tutor002.
     * Using current timestamps relative to now
     */
    private List<ConnectionData> getTutor002Connections() {
        Instant now = Instant.now();
        return List.of(
                // Accepted connections (2)
                new ConnectionData("tutor002", "student004", StatusRequest.ACCEPTED, "Want to learn mobile app development with Flutter.", now.minusSeconds(22 * 24 * 3600).toString()),
                new ConnectionData("tutor002", "student005", StatusRequest.ACCEPTED, "Need guidance on building iOS apps with Swift.", now.minusSeconds(18 * 24 * 3600).toString()),

                // Pending connection (1)
                new ConnectionData("tutor002", "student006", StatusRequest.PENDING, "Looking to master React Native for cross-platform development.", now.minusSeconds(36 * 3600).toString())
        );
    }

    /**
     * Get all bootstrap connections for tutor003.
     * Using current timestamps relative to now
     */
    private List<ConnectionData> getTutor003Connections() {
        Instant now = Instant.now();
        return List.of(
                // Accepted connections (2)
                new ConnectionData("tutor003", "student007", StatusRequest.ACCEPTED, "Need help preparing for coding interviews at big tech companies.", now.minusSeconds(19 * 24 * 3600).toString()),
                new ConnectionData("tutor003", "student008", StatusRequest.ACCEPTED, "Want to improve my competitive programming skills.", now.minusSeconds(14 * 24 * 3600).toString()),

                // Pending connection (1)
                new ConnectionData("tutor003", "student009", StatusRequest.PENDING, "Looking for guidance on dynamic programming problems.", now.minusSeconds(48 * 3600).toString())
        );
    }

    /**
     * Get all bootstrap connections for tutor004.
     * Using current timestamps relative to now
     */
    private List<ConnectionData> getTutor004Connections() {
        Instant now = Instant.now();
        return List.of(
                // Accepted connection (1)
                new ConnectionData("tutor004", "student010", StatusRequest.ACCEPTED, "Want to learn full-stack development from scratch.", now.minusSeconds(21 * 24 * 3600).toString()),

                // Pending connections (2)
                new ConnectionData("tutor004", "student011", StatusRequest.PENDING, "Need guidance on React and Spring Boot project.", now.minusSeconds(3 * 24 * 3600).toString()),
                new ConnectionData("tutor004", "student012", StatusRequest.PENDING, "Looking to improve my web development skills.", now.minusSeconds(15 * 3600).toString())
        );
    }

    /**
     * Get all bootstrap connections for tutor005.
     * Using current timestamps relative to now
     */
    private List<ConnectionData> getTutor005Connections() {
        Instant now = Instant.now();
        return List.of(
                // No connections for tutor005 with students 001-012
        );
    }

    /**
     * Create connections for a specific tutor.
     */
    private void createConnectionsForTutor(List<ConnectionData> connections) {
        for (ConnectionData connection : connections) {
            createConnection(
                    helper.generateConnectionId(),
                    connection.tutorId,
                    connection.studentId,
                    connection.status,
                    connection.message,
                    helper.parseTimestamp(connection.timestamp)
            );
        }
    }

    /**
     * Create a single connection.
     */
    private void createConnection(String connectionId, String tutorId, String studentId,
                                  StatusRequest status, String message, Instant timestamp) {
        try {
            // Check if connection already exists
            if (connectionRepository.existsConnection(connectionId)) {
                return;
            }

            Connection connectionEntity = new Connection(connectionId, tutorId, studentId, status, message, timestamp);
            connectionRepository.save(connectionEntity);
        } catch (Exception e) {
            System.err.println("Error creating connection " + connectionId + ": " + e.getMessage());
        }
    }
}
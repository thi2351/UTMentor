package com.example.utmentor.infrastructures.repository.Interface;

import com.example.utmentor.models.docEntities.Connection.Connection;

import com.example.utmentor.models.docEntities.Connection.StatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;


@Repository
public class ConnectionRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ConnectionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public Connection findFirstByTutorIdAndStudentId(String tutorId, String studentId)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("tutorId").is(tutorId)
                .and("studentId").is(studentId));
        query.limit(1);
        return mongoTemplate.findOne(query, Connection.class);
    }

    public Connection createConnection(String tutorId, String studentId, String message) {
        Connection connection = new Connection(
                UUID.randomUUID().toString(),
                tutorId,
                studentId,
                StatusRequest.PENDING,
                message,
                Instant.now()
        );
        Connection saved = mongoTemplate.save(connection);
        return saved;
    }
    public boolean updateConnection(Connection connection , String message) {
        connection.setStatus(StatusRequest.PENDING);
        connection.setMessage(message);
        Connection saved = mongoTemplate.save(connection);
        return saved.getId() != null;
    }

    public boolean existsConnection(String connectionId) {
        return mongoTemplate.exists(new Query(Criteria.where("_id").is(connectionId)), Connection.class);
    }

    public boolean save(Connection connectionEntity) {
        boolean exists = mongoTemplate.exists(
                new Query(Criteria.where("id").is(connectionEntity.getId())),
                Connection.class
        );
        if (exists) {
            return false;
        }

        mongoTemplate.save(connectionEntity);
        return true;
    }



}
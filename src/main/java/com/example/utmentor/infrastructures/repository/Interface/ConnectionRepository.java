package com.example.utmentor.infrastructures.repository.Interface;

import com.example.utmentor.models.docEntities.Connection.Connection;

import com.example.utmentor.models.docEntities.Connection.StatusRequest;
import com.example.utmentor.models.webModels.connections.GetPendingRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Repository
public class ConnectionRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ConnectionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Connection getConnection(String id) {
        return mongoTemplate.findById(id, Connection.class);
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

    public void update(Connection connectionEntity) {
        mongoTemplate.save(connectionEntity);
    }


    public List<GetPendingRequestResponse> getPendingRequests(String tutorId) {

        List<AggregationOperation> pipeline = new ArrayList<>();

        pipeline.add(
                Aggregation.match(
                        Criteria.where("tutorId").is(tutorId)
                                .and("status").is(StatusRequest.PENDING)
                )
        );
        pipeline.add(
                Aggregation.lookup(
                        "users",
                        "studentId",
                        "_id",
                        "studentDetails"
                )
        );
        pipeline.add(Aggregation.unwind("studentDetails", true));
        pipeline.add(
                Aggregation.project()
                        .and("_id").as("connectionId")
                        .and("studentDetails._id").as("studentId")
                        .and("message").as("message")
                        .and("createdAt").as("requestDate")
                        .and("studentDetails.avatarUrl").as("studentAvatar")
                        .andExpression("concat(studentDetails.lastName, ' ', studentDetails.firstName)").as("studentName")

        );


        Aggregation aggregation = Aggregation.newAggregation(pipeline);

        List<GetPendingRequestResponse> results = mongoTemplate.aggregate(
                aggregation,
                "connections",
                GetPendingRequestResponse.class
        ).getMappedResults();

        return results;

    }

}
package com.example.utmentor.infrastructures.repository.Interface;



import com.example.utmentor.models.docEntities.Notification.Notification;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public NotificationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Notification findById(String id) {
        return mongoTemplate.findById(id, Notification.class);
    }

    public void markAsRead( List<String> notificationIds) {

        Query query = new Query(Criteria.where("_id").in(notificationIds));
        Update update = new Update().set("isRead", true);

        UpdateResult result = mongoTemplate.updateMulti(query, update, Notification.class);

    }


    public void save(Notification notification) {
        Notification saved = mongoTemplate.save(notification);
    }

    public List<Notification> findByToUserId(String userId) {
        Query query = new Query(Criteria.where("toUserId").is(userId));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, Notification.class);
    }
}


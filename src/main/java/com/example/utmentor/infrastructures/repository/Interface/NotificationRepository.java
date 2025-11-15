package com.example.utmentor.infrastructures.repository.Interface;



import com.example.utmentor.models.docEntities.Notification.Notification;
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

    public boolean markAsRead(String notificationId) {
        Query query = new Query(Criteria.where("_id").is(notificationId));
        Update update = new Update().set("read", true);

        var result = mongoTemplate.updateFirst(query, update, Notification.class);

        return result.getModifiedCount() > 0;
    }

    public void save(Notification notification) {
        Notification saved = mongoTemplate.save(notification);
    }

    public List<Notification> findByToUserIdOrderByIdDesc(String userId) {
        Query query = new Query(Criteria.where("toUserId").is(userId));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "_id"));
        return mongoTemplate.find(query, Notification.class);
    }
}


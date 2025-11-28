package com.example.utmentor.infrastructures.repository.Interface;

import com.example.utmentor.models.docEntities.Communities.Communities;
import com.example.utmentor.models.webModels.communities.GetCommunitiesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@Repository
public class CommunitiesRepository {

    private final MongoTemplate mongoTemplate;

    public CommunitiesRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(Communities communities) {
        mongoTemplate.save(communities);
    }

    public Communities findByHost(String tutorId) {
        Query query = new Query(Criteria.where("host").is(tutorId));
        return mongoTemplate.findOne(query, Communities.class);

    }

    public List<GetCommunitiesResponse> findCommunitiesInfoByHostId(String userId) {
        Query query = new Query(Criteria.where("host").is(userId));
        List<Communities> communitiesList = mongoTemplate.find(query, Communities.class);

        return communitiesList.stream().map(community -> new GetCommunitiesResponse(
                community.getId(),
                community.getName(),
                community.getStudentCount(),
                community.isActive()
        )).toList();
    }
}

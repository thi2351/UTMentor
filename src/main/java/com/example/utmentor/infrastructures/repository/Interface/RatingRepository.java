package com.example.utmentor.infrastructures.repository.Interface;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.utmentor.models.docEntities.Rating;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.profile.ReviewResponse;

@Repository
public class RatingRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public List<Rating> findByRevieweeID(String revieweeID) {
        Query query = new Query(Criteria.where("revieweeID").is(revieweeID));
        return mongoTemplate.find(query, Rating.class);
    }
    
    public Page<Rating> findByRevieweeID(String revieweeID, Pageable pageable) {
        Query query = new Query(Criteria.where("revieweeID").is(revieweeID));
        query.with(pageable);
        
        List<Rating> ratings = mongoTemplate.find(query, Rating.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Rating.class);
        
        return new PageImpl<>(ratings, pageable, total);
    }
    
    public Optional<Rating> findById(String id) {
        Rating rating = mongoTemplate.findById(id, Rating.class);
        return Optional.ofNullable(rating);
    }
    
    public boolean existsById(String id) {
        return mongoTemplate.exists(new Query(Criteria.where("_id").is(id)), Rating.class);
    }
    
    public Rating save(Rating rating) {
        return mongoTemplate.save(rating);
    }
    
    public void deleteById(String id) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), Rating.class);
    }
    
    public List<Rating> findAll() {
        return mongoTemplate.findAll(Rating.class);
    }
    
    public PageResponse<ReviewResponse> findTutorReviewsWithReviewerInfo(
            String tutorId,
            Pageable pageable,
            String sort) {
        
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = Math.min(Math.max(1, pageable.getPageSize()), 100);
        long skip = pageable.getOffset();

        List<AggregationOperation> filterPipeline = new ArrayList<>();

        MatchOperation matchRating = Aggregation.match(
                Criteria.where("revieweeID").is(tutorId)
        );
        filterPipeline.add(matchRating);

        LookupOperation lookupReviewer = Aggregation.lookup(
                "users",
                "reviewerID",
                "_id",
                "reviewerDetails"
        );
        filterPipeline.add(lookupReviewer);

        filterPipeline.add(Aggregation.unwind("reviewerDetails", true));

        List<AggregationOperation> countOps = new ArrayList<>(filterPipeline);
        countOps.add(Aggregation.count().as("totalCount"));

        TypedAggregation<Rating> countAggregation = Aggregation.newAggregation(
                Rating.class,
                countOps
        );

        AggregationResults<Document> countResult = mongoTemplate.aggregate(
                countAggregation,
                Document.class
        );

        Document countDoc = countResult.getUniqueMappedResult();
        long total = 0;
        if (countDoc != null && countDoc.containsKey("totalCount")) {
            Object countValue = countDoc.get("totalCount");
            if (countValue instanceof Number num) {
                total = num.longValue();
            }
        }

        if (total == 0) {
            return new PageResponse<>(new ArrayList<>(), pageNumber, pageSize, 0);
        }

        List<AggregationOperation> dataOps = new ArrayList<>(filterPipeline);

        Sort sortOrder = getSortOrder(sort, pageable.getSort());
        dataOps.add(Aggregation.sort(sortOrder));

        dataOps.add(Aggregation.skip(skip));
        dataOps.add(Aggregation.limit(pageSize));

        ProjectionOperation project = Aggregation.project()
                .and("_id").as("reviewID")
                .and("reviewerDetails.firstName").as("reviewerFirstName")
                .and("reviewerDetails.lastName").as("reviewerLastName")
                .and("reviewerDetails.avatarUrl").as("reviewerAvatarUrl")
                .and("rating").as("rating")
                .and("comment").as("comment")
                .and("timestamp").as("timestamp");

        dataOps.add(project);

        TypedAggregation<Rating> dataAggregation = Aggregation.newAggregation(
                Rating.class,
                dataOps
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                dataAggregation,
                Document.class
        );

        List<Document> resultDocuments = results.getMappedResults();

        List<ReviewResponse> reviewResponses = resultDocuments.stream()
                .map(this::convertDocumentToReviewResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                reviewResponses,
                pageNumber,
                pageSize,
                total
        );
    }

    private Sort getSortOrder(String sort, Sort pageableSort) {
        Sort customSort = switch (sort == null ? "latest" : sort) {
            case "rating-descending" -> Sort.by(Sort.Direction.DESC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "timestamp"));
            case "rating-ascending" -> Sort.by(Sort.Direction.ASC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "timestamp"));
            default -> Sort.by(Sort.Direction.DESC, "timestamp");
        };
        
        if (pageableSort != null && pageableSort.isSorted()) {
            return customSort.and(pageableSort);
        }
        return customSort;
    }

    private ReviewResponse convertDocumentToReviewResponse(Document doc) {
        String reviewID = doc.getString("reviewID");
        
        String reviewerFirstName = doc.getString("reviewerFirstName");
        String reviewerLastName = doc.getString("reviewerLastName");
        String reviewerName = (reviewerFirstName != null && reviewerLastName != null)
                ? reviewerFirstName + " " + reviewerLastName
                : (reviewerFirstName != null ? reviewerFirstName : "Unknown");
        
        String reviewerAvatarUrl = doc.getString("reviewerAvatarUrl");
        
        Integer rating = doc.getInteger("rating");
        String comment = doc.getString("comment");
        
        Instant timestamp = null;
        Object timestampObj = doc.get("timestamp");
        if (timestampObj instanceof Instant instant) {
            timestamp = instant;
        } else if (timestampObj instanceof org.bson.BsonTimestamp bsonTimestamp) {
            timestamp = Instant.ofEpochSecond(bsonTimestamp.getTime());
        } else if (timestampObj != null) {
            timestamp = Instant.now();
        }

        return new ReviewResponse(
                reviewID,
                reviewerName,
                reviewerAvatarUrl,
                rating,
                comment,
                timestamp != null ? timestamp : Instant.now()
        );
    }
}

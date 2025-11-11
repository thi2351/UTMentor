package com.example.utmentor.infrastructures.repository.search;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.stereotype.Repository;

import com.example.utmentor.models.docEntities.Rating;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.profile.ReviewResponse;

@Repository
public class RatingSearchRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageResponse<ReviewResponse> findTutorReviewsWithReviewerInfo(
            String tutorId,
            int page,
            int pageSize,
            String sort) {
        
        int p = Math.max(1, page);
        int sz = Math.min(Math.max(1, pageSize), 100);
        long skip = (long) (p - 1) * sz;


        List<AggregationOperation> filterPipeline = new ArrayList<>();

        // Match ratings for the specific tutor
        MatchOperation matchRating = Aggregation.match(
                Criteria.where("revieweeID").is(tutorId)
        );
        filterPipeline.add(matchRating);

        // Lookup to join with users collection to get reviewer info
        // Spring Data MongoDB maps @Id String id to "_id" in MongoDB
        LookupOperation lookupReviewer = Aggregation.lookup(
                "users",
                "reviewerID",
                "_id",  // MongoDB uses "_id" as the primary key field
                "reviewerDetails"
        );
        filterPipeline.add(lookupReviewer);

        // Unwind reviewerDetails array (should have 0 or 1 element)
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

        // Early return if no results
        if (total == 0) {
            return new PageResponse<>(new ArrayList<>(), p, sz, 0, 0, false);
        }


        List<AggregationOperation> dataOps = new ArrayList<>(filterPipeline);

        // Add Sort stage
        Sort sortOrder = getSortOrder(sort);
        dataOps.add(Aggregation.sort(sortOrder));

        // Add Pagination stages
        dataOps.add(Aggregation.skip(skip));
        dataOps.add(Aggregation.limit(sz));

        // Project fields to match ReviewResponse structure
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

        int totalPages = (int) Math.ceil((double) total / sz);
        boolean hasNext = p < totalPages;

        return new PageResponse<>(
                reviewResponses,
                p,
                sz,
                total,
                totalPages,
                hasNext
        );
    }

    private Sort getSortOrder(String sort) {
        return switch (sort == null ? "latest" : sort) {
            case "rating-descending" -> Sort.by(Sort.Direction.DESC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "timestamp"));
            case "rating-ascending" -> Sort.by(Sort.Direction.ASC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "timestamp"));
            default -> Sort.by(Sort.Direction.DESC, "timestamp");
        };
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
            // Try to parse as string or other formats if needed
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


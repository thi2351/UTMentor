package com.example.utmentor.infrastructures.repository.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

// Import Collation từ Spring Data MongoDB
import org.springframework.data.mongodb.core.query.Collation;

import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.search.TutorListItem;

@Repository
public class TutorSearchRepo {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Tạo Collation cho tiếng Việt sử dụng Spring Data MongoDB
    private static final Collation VIETNAMESE_COLLATION = Collation.of("vi");

    public PageResponse<TutorListItem> search(
            Department department,
            List<Expertise> expertiseList,
            String sortKey,
            Integer page,
            Integer pageSize
    ) {
        int p = Math.max(1, page);
        int sz = Math.min(Math.max(1, pageSize), 100);
        long skip = (long) (p - 1) * sz;

        // Build filter pipeline
        List<AggregationOperation> filterPipeline = new ArrayList<>();

        List<Criteria> tutorCriteriaList = new ArrayList<>();

        Criteria capacityCriteria = Criteria.where("$expr").is(
                ComparisonOperators.Lt.valueOf("$currentMenteeCount").lessThan("$maximumCapacity")
        );
        tutorCriteriaList.add(capacityCriteria);

        if (expertiseList != null && !expertiseList.isEmpty()) {
            List<String> expertiseNames = expertiseList.stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
            tutorCriteriaList.add(Criteria.where("expertise").all(expertiseNames));
        }

        MatchOperation matchTutorProfile = Aggregation.match(new Criteria().andOperator(tutorCriteriaList));
        filterPipeline.add(matchTutorProfile);

        LookupOperation lookupUser = Aggregation.lookup("users", "_id", "_id", "userDetails");
        filterPipeline.add(lookupUser);

        filterPipeline.add(Aggregation.unwind("userDetails", true));

        if (department != null) {
            MatchOperation matchUserDepartment = Aggregation.match(
                    Criteria.where("userDetails.department").is(department.name())
            );
            filterPipeline.add(matchUserDepartment);
        }

        // Count Pipeline với Collation
        List<AggregationOperation> countOps = new ArrayList<>(filterPipeline);
        countOps.add(Aggregation.count().as("totalCount"));

        TypedAggregation<TutorProfile> countAggregation = Aggregation.newAggregation(TutorProfile.class, countOps)
                .withOptions(Aggregation.newAggregationOptions()
                        .collation(VIETNAMESE_COLLATION)
                        .build());

        AggregationResults<Document> countResult = mongoTemplate.aggregate(countAggregation, Document.class);

        Document countDoc = countResult.getUniqueMappedResult();
        long total = 0;
        if (countDoc != null && countDoc.containsKey("totalCount")) {
            Object countValue = countDoc.get("totalCount");
            if (countValue instanceof Number num) {
                total = num.longValue();
            }
        }

        if (total == 0) {
            return new PageResponse<>(new ArrayList<>(), p, sz, 0, 0, false);
        }

        // Data Pipeline với Collation
        List<AggregationOperation> dataOps = new ArrayList<>(filterPipeline);

        Sort sort = getSort(sortKey);
        dataOps.add(Aggregation.sort(sort));

        dataOps.add(Aggregation.skip(skip));
        dataOps.add(Aggregation.limit(sz));

        TypedAggregation<TutorProfile> dataAggregation = Aggregation.newAggregation(TutorProfile.class, dataOps)
                .withOptions(Aggregation.newAggregationOptions()
                        .collation(VIETNAMESE_COLLATION)
                        .build());

        AggregationResults<Document> results = mongoTemplate.aggregate(dataAggregation, Document.class);
        List<Document> resultDocuments = results.getMappedResults();

        List<TutorListItem> items = resultDocuments.stream()
                .map(this::convertDocumentToTutorListItem)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) total / sz);
        boolean hasNext = p < totalPages;

        return new PageResponse<>(items, p, sz, total, totalPages, hasNext);
    }

    private Sort getSort(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            sortKey = "rating-descending";
        }

        return switch (sortKey.toLowerCase()) {
            case "rating-ascending" ->
                    Sort.by(Sort.Order.asc("ratingAvg"), Sort.Order.asc("ratingCount"));
            case "firstname-ascending" ->
                    Sort.by(Sort.Order.asc("userDetails.firstName"));
            case "firstname-descending" ->
                    Sort.by(Sort.Order.desc("userDetails.firstName"));
            case "tutor-time" ->
                    Sort.by(Sort.Order.desc("ratingCount"), Sort.Order.desc("ratingAvg"));
            default ->
                    Sort.by(Sort.Order.desc("ratingAvg"), Sort.Order.desc("ratingCount"));
        };
    }

    private TutorListItem convertDocumentToTutorListItem(Document doc) {
        String id = null;
        Object idObject = doc.get("_id");
        if (idObject instanceof ObjectId) {
            id = ((ObjectId) idObject).toHexString();
        } else if (idObject instanceof String s) {
            id = s;
        }

        List<Expertise> expertise = List.of();
        Object expertiseObj = doc.get("expertise");
        if (expertiseObj instanceof List<?> expList) {
            expertise = expList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(name -> {
                        try {
                            return Expertise.valueOf(name);
                        } catch (IllegalArgumentException e) {
                            System.err.println("WARN: Invalid expertise value found in DB: " + name);
                            return null;
                        }
                    })
                    .filter(e -> e != null)
                    .collect(Collectors.toList());
        }

        Integer ratingCount = doc.getInteger("ratingCount", 0);
        Double ratingAvg = doc.get("ratingAvg", Double.class);
        if (ratingAvg == null) ratingAvg = 0.0;

        Integer currentMentees = doc.getInteger("currentMenteeCount", 0);
        Integer maxCapacity = doc.getInteger("maximumCapacity", 0);

        Document userDetails = doc.get("userDetails", Document.class);
        String firstName = "";
        String lastName = "";
        String avatarUrl = null;
        Department departmentEnum = null;
        String description = null;

        if (userDetails != null) {
            firstName = userDetails.getString("firstName");
            lastName = userDetails.getString("lastName");
            avatarUrl = userDetails.getString("avatarUrl");
            String departmentStr = userDetails.getString("department");
            if (departmentStr != null) {
                try {
                    departmentEnum = Department.valueOf(departmentStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("WARN: Invalid department value found in DB: " + departmentStr);
                }
            }
        }

        return new TutorListItem(
                id,
                firstName,
                lastName,
                avatarUrl,
                departmentEnum,
                expertise,
                ratingCount,
                ratingAvg,
                currentMentees,
                maxCapacity,
                description
        );
    }
}
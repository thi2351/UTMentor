package com.example.utmentor.infrastructures.repository.search;

import java.util.ArrayList;
import java.util.Arrays; // Keep this import
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId; // Keep this import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate; // Ensure this import is present
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators; // Import for comparing fields
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation; // Use TypedAggregation for clarity
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

// Keep these imports
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.search.TutorListItem;

@Repository
public class TutorSearchRepo {

    @Autowired
    private UserRepository userRepository; // Keep if needed for conversion, though aggregation is better

    @Autowired
    private MongoTemplate mongoTemplate;

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

        // ============================================================
        // 1️⃣ Build the common filter pipeline stages
        // ============================================================
        List<AggregationOperation> filterPipeline = new ArrayList<>();

        // --- Match TutorProfile Stage ---
        List<Criteria> tutorCriteriaList = new ArrayList<>();
//        tutorCriteriaList.add(Criteria.where("isActive").is(true));

        // CORRECT way to compare two fields using $expr
        Criteria capacityCriteria = Criteria.where("$expr").is(
                ComparisonOperators.Lt.valueOf("$currentMenteeCount").lessThan("$maximumCapacity")
        );
        tutorCriteriaList.add(capacityCriteria); // Add the $expr criteria

        // Add expertise filter if provided
        if (expertiseList != null && !expertiseList.isEmpty()) {
            List<String> expertiseNames = expertiseList.stream()
                    .map(Enum::name) // Convert Enum to String
                    .collect(Collectors.toList());
            tutorCriteriaList.add(Criteria.where("expertise").all(expertiseNames)); // Use List<String>
        }

        // Combine all TutorProfile criteria into one $match stage
        MatchOperation matchTutorProfile = Aggregation.match(new Criteria().andOperator(tutorCriteriaList));
        filterPipeline.add(matchTutorProfile);

        // --- $lookup stage to join with users collection ---
        // Ensure "users" is the correct collection name in your MongoDB
        LookupOperation lookupUser = Aggregation.lookup("users", "_id", "_id", "userDetails");
        filterPipeline.add(lookupUser);

        // --- $unwind stage to deconstruct the userDetails array ---
        // Using preserveNullAndEmptyArrays = true keeps tutors even if user data is missing (adjust if needed)
        filterPipeline.add(Aggregation.unwind("userDetails", true));

        // --- Match User Stage (if department filter is present) ---
        if (department != null) {
            // Filter based on the 'department' field inside the joined 'userDetails'
            MatchOperation matchUserDepartment = Aggregation.match(
                    Criteria.where("userDetails.department").is(department.name()) // Compare with Enum name (String)
            );
            filterPipeline.add(matchUserDepartment);
            // You could add other user-related filters here if needed, e.g.:
            // Criteria.where("userDetails.isActive").is(true)
        }

        // ============================================================
        // 2️⃣ Build and Execute the Count Pipeline
        // ============================================================
        List<AggregationOperation> countOps = new ArrayList<>(filterPipeline); // Copy filter stages
        countOps.add(Aggregation.count().as("totalCount")); // Add the $count stage

        // Use TypedAggregation for better type safety if possible, otherwise keep Aggregation
        TypedAggregation<TutorProfile> countAggregation = Aggregation.newAggregation(TutorProfile.class, countOps);

        AggregationResults<Document> countResult = mongoTemplate.aggregate(countAggregation, Document.class); // Output type is Document

        Document countDoc = countResult.getUniqueMappedResult();
        long total = 0; // Default to 0
        if (countDoc != null && countDoc.containsKey("totalCount")) {
            // Safely get the count value
            Object countValue = countDoc.get("totalCount");
            if (countValue instanceof Number num) { // Check if it's a number
                total = num.longValue();
            }
        }

        // Optimization: If count is 0, return early
        if (total == 0) {
            return new PageResponse<>(new ArrayList<>(), p, sz, 0, 0, false);
        }

        // ============================================================
        // 3️⃣ Build and Execute the Data Pipeline (with Sort & Pagination)
        // ============================================================
        List<AggregationOperation> dataOps = new ArrayList<>(filterPipeline); // Copy filter stages again

        // Add Sort stage
        Sort sort = getSort(sortKey);
        dataOps.add(Aggregation.sort(sort));

        // Add Pagination stages ($skip, $limit)
        dataOps.add(Aggregation.skip(skip));
        dataOps.add(Aggregation.limit(sz));

        // Use TypedAggregation if possible
        TypedAggregation<TutorProfile> dataAggregation = Aggregation.newAggregation(TutorProfile.class, dataOps);

        AggregationResults<Document> results = mongoTemplate.aggregate(dataAggregation, Document.class); // Output type is Document
        List<Document> resultDocuments = results.getMappedResults();

        // ============================================================
        // 4️⃣ Convert results to DTO
        // ============================================================
        List<TutorListItem> items = resultDocuments.stream()
                .map(this::convertDocumentToTutorListItem) // Use method reference
                .collect(Collectors.toList());

        // ============================================================
        // 5️⃣ Calculate pagination info and return
        // ============================================================
        int totalPages = (int) Math.ceil((double) total / sz);
        boolean hasNext = p < totalPages;

        return new PageResponse<>(items, p, sz, total, totalPages, hasNext);
    }

    // ============================================================
    // 🔸 Sort logic (Keep as is, seems correct now)
    // ============================================================
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
            case "tutor-time" -> // Consider if this logic is still needed/correct
                    Sort.by(Sort.Order.desc("ratingCount"), Sort.Order.desc("ratingAvg"));
            default -> // Default to rating-descending
                    Sort.by(Sort.Order.desc("ratingAvg"), Sort.Order.desc("ratingCount"));
        };
    }

    // ============================================================
    // 🔸 Convert Document → DTO (Keep as is, seems robust now)
    // ============================================================
    private TutorListItem convertDocumentToTutorListItem(Document doc) {
        // TutorProfile info
        String id = null;
        Object idObject = doc.get("_id"); // Get as Object first
        if (idObject instanceof ObjectId) {
            id = ((ObjectId) idObject).toHexString();
        } else if (idObject instanceof String s) {
            id = s;
        }
        // Add more checks if _id could be other types

        List<Expertise> expertise = List.of(); // Default to empty list
        // Safely get the list and handle potential type issues or null values
        Object expertiseObj = doc.get("expertise");
        if (expertiseObj instanceof List<?> expList) {
            expertise = expList.stream()
                    .filter(String.class::isInstance) // Ensure elements are Strings
                    .map(String.class::cast)
                    .map(name -> {
                        try {
                            return Expertise.valueOf(name);
                        } catch (IllegalArgumentException e) {
                            System.err.println("WARN: Invalid expertise value found in DB: " + name); // Log invalid values
                            return null; // Skip invalid values
                        }
                    })
                    .filter(e -> e != null) // Remove nulls resulting from invalid values
                    .collect(Collectors.toList());
        }


        Integer ratingCount = doc.getInteger("ratingCount", 0); // Use default value if missing
        Double ratingAvg = doc.get("ratingAvg", Double.class); // Get as specific type
        if (ratingAvg == null) ratingAvg = 0.0; // Handle null explicitly

        Integer currentMentees = doc.getInteger("currentMenteeCount", 0);
        Integer maxCapacity = doc.getInteger("maximumCapacity", 0);


        // User details
        Document userDetails = doc.get("userDetails", Document.class); // Get as Document
        String firstName = "";
        String lastName = "";
        String avatarUrl = null;
        Department departmentEnum = null; // Renamed variable to avoid conflict
        String description = null;

        if (userDetails != null) {
            firstName = userDetails.getString("firstName"); // getString handles null safely
            lastName = userDetails.getString("lastName");
            avatarUrl = userDetails.getString("avatarUrl");
            String departmentStr = userDetails.getString("department");
            if (departmentStr != null) {
                try {
                    departmentEnum = Department.valueOf(departmentStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("WARN: Invalid department value found in DB: " + departmentStr); // Log invalid values
                    // Keep departmentEnum as null
                }
            }
            // description = userDetails.getString("description"); // Uncomment if User has description
        }

        return new TutorListItem(
                id,
                firstName,
                lastName,
                avatarUrl,
                departmentEnum, // Use the correctly parsed Enum or null
                expertise,
                ratingCount,
                ratingAvg,
                currentMentees,
                maxCapacity,
                description
        );
    }
}
package com.example.utmentor.models.docEntities.users;

    import java.time.Instant;
    import java.util.List;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

    import com.example.utmentor.models.docEntities.Expertise;
    import com.example.utmentor.models.webModels.profile.Achievement;

    import jakarta.validation.constraints.NotBlank;

    @Document(collection = "tutorProfiles")
    public class TutorProfile {
        @Id
        @NotBlank
        private String id;

        private List<Expertise> expertise;

        private String tutorDescription;

        private Integer yearsOfExperience;

        private Integer totalStudentTaught;

        private List<Achievement> achievements;

        private boolean isActive = true;

        private Integer maximumCapacity;

        private Integer currentMenteeCount = 0;

        private Integer ratingCount = 0;

        private Double ratingAvg = 0.0;

        private Instant createdAt = Instant.now();

        private Instant updatedAt = Instant.now();

        public TutorProfile() {}

        public TutorProfile(String id, List<Expertise> expertise,
                           boolean isActive, Integer maximumCapacity, Integer currentMenteeCount) {
            this.id = id;
            this.expertise = expertise;
            this.isActive = isActive;
            this.maximumCapacity = maximumCapacity;
            this.currentMenteeCount = currentMenteeCount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
            this.updatedAt = Instant.now();
        }

        public List<Expertise> getExpertise() {
            return expertise;
        }

        public void setExpertise(List<Expertise> expertise) {
            this.expertise = expertise;
            this.updatedAt = Instant.now();
        }

        public String getTutorDescription() {
            return tutorDescription;
        }

        public void setTutorDescription(String tutorDescription) {
            this.tutorDescription = tutorDescription;
            this.updatedAt = Instant.now();
        }

        public Integer getYearsOfExperience() {
            return yearsOfExperience;
        }

        public void setYearsOfExperience(Integer yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
            this.updatedAt = Instant.now();
        }

        public Integer getTotalStudentTaught() {
            return totalStudentTaught;
        }

        public void setTotalStudentTaught(Integer totalStudentTaught) {
            this.totalStudentTaught = totalStudentTaught;
            this.updatedAt = Instant.now();
        }

        public List<Achievement> getAchievements() {
            return achievements;
        }

        public void setAchievements(List<Achievement> achievements) {
            this.achievements = achievements;
            this.updatedAt = Instant.now();
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            this.isActive = active;
            this.updatedAt = Instant.now();
        }

        public Integer getMaximumCapacity() {
            return maximumCapacity;
        }

        public void setMaximumCapacity(Integer maximumCapacity) {
            this.maximumCapacity = maximumCapacity;
            this.updatedAt = Instant.now();
        }

        public Integer getCurrentMenteeCount() {
            return currentMenteeCount;
        }

        public void setCurrentMenteeCount(Integer currentMenteeCount) {
            this.currentMenteeCount = currentMenteeCount;
            this.updatedAt = Instant.now();
        }

        public Integer getRatingCount() {
            return ratingCount;
        }

        public void setRatingCount(Integer ratingCount) {
            this.ratingCount = ratingCount != null ? ratingCount : 0;
            this.updatedAt = Instant.now();
        }

        public Double getRatingAvg() {
            return ratingAvg;
        }

        public void setRatingAvg(Double ratingAvg) {
            this.ratingAvg = ratingAvg != null ? ratingAvg : 0.0;
            this.updatedAt = Instant.now();
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        public Instant getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
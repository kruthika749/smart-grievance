package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CommunityIssueService {

    private static final int COMMUNITY_THRESHOLD = 3;

    private final GrievanceRepository grievanceRepository;

    public CommunityIssueService(
            GrievanceRepository grievanceRepository) {

        this.grievanceRepository =
                grievanceRepository;
    }

    public CommunityIssueResult analyzeCommunityIssue(
            String location,
            String category) {

        if (location == null ||
                location.trim().isEmpty() ||
                category == null ||
                category.trim().isEmpty()) {

            return new CommunityIssueResult(
                    false,
                    0,
                    "Insufficient location or category information."
            );
        }

        List<Grievance> relatedGrievances =
                grievanceRepository
                        .findByLocationIgnoreCaseAndCategoryIgnoreCase(
                                location.trim(),
                                category.trim()
                        );

        int count =
                relatedGrievances.size();

        if (count >= COMMUNITY_THRESHOLD) {

            return new CommunityIssueResult(
                    true,
                    count,
                    "Multiple grievances with the same category "
                    + "have been reported from this location."
            );
        }

        return new CommunityIssueResult(
                false,
                count,
                "No community-level pattern detected yet."
        );
    }

    public static class CommunityIssueResult {

        private boolean communityIssueDetected;
        private int relatedGrievanceCount;
        private String message;

        public CommunityIssueResult(
                boolean communityIssueDetected,
                int relatedGrievanceCount,
                String message) {

            this.communityIssueDetected =
                    communityIssueDetected;

            this.relatedGrievanceCount =
                    relatedGrievanceCount;

            this.message = message;
        }

        public boolean isCommunityIssueDetected() {
            return communityIssueDetected;
        }

        public int getRelatedGrievanceCount() {
            return relatedGrievanceCount;
        }

        public String getMessage() {
            return message;
        }
    }
}
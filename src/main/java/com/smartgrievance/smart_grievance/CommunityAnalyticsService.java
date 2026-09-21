package com.smartgrievance.smart_grievance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CommunityAnalyticsService {

    private final GrievanceRepository grievanceRepository;

    public CommunityAnalyticsService(
            GrievanceRepository grievanceRepository) {

        this.grievanceRepository =
                grievanceRepository;
    }

    public CommunityAnalytics analyzeCommunity(
            String location) {

        if (location == null ||
                location.trim().isEmpty()) {

                    return new CommunityAnalytics(
                        "",
                        0,
                        0,
                        0,
                        0,
                        0,
                        new LinkedHashMap<>(),
                        new LinkedHashMap<>(),
                        "Location information is not available."
                );
        }

        List<Grievance> grievances =
                grievanceRepository.findByLocationIgnoreCase(
                        location.trim()
                );

        int total = grievances.size();

        int pending = 0;
        int inProgress = 0;
        int resolved = 0;
        int escalated = 0;

        Map<String, Integer> categoryCounts =
                new LinkedHashMap<>();

        Map<String, Integer> priorityCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String status = grievance.getStatus();

            if (status != null) {

                if ("Pending".equalsIgnoreCase(status)) {
                    pending++;
                }

                if ("In Progress".equalsIgnoreCase(status)) {
                    inProgress++;
                }

                if ("Resolved".equalsIgnoreCase(status)) {
                    resolved++;
                }
            }

            if ("Escalated".equalsIgnoreCase(
                    grievance.getEscalationStatus())) {

                escalated++;
            }

            String category = grievance.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Unclassified";
            }

            categoryCounts.put(
                    category,
                    categoryCounts.getOrDefault(category, 0) + 1
            );

            String priority = grievance.getPriority();

            if (priority == null ||
                    priority.trim().isEmpty()) {

                priority = "Unclassified";
            }

            priorityCounts.put(
                    priority,
                    priorityCounts.getOrDefault(priority, 0) + 1
            );
        }

        String message;

        if (total == 0) {

            message =
                    "No grievances have been reported "
                    + "from this location.";

        } else {

            message =
                    "Community analytics generated from "
                    + total
                    + " grievance(s) reported at this location.";
        }

        return new CommunityAnalytics(
                location.trim(),
                total,
                pending,
                inProgress,
                resolved,
                escalated,
                categoryCounts,
                priorityCounts,
                message
        );
    }

    public static class CommunityAnalytics {

        private String location;
        private int totalGrievances;
        private int pending;
        private int inProgress;
        private int resolved;
        private int escalated;
        private Map<String, Integer> categoryCounts;
        private Map<String, Integer> priorityCounts;
        private String message;

        public CommunityAnalytics(
                String location,
                int totalGrievances,
                int pending,
                int inProgress,
                int resolved,
                int escalated,
                Map<String, Integer> categoryCounts,
                Map<String, Integer> priorityCounts,
                String message) {

            this.location = location;
            this.totalGrievances = totalGrievances;
            this.pending = pending;
            this.inProgress = inProgress;
            this.resolved = resolved;
            this.escalated = escalated;
            this.categoryCounts = categoryCounts;
            this.priorityCounts = priorityCounts;
            this.message = message;
        }

        public String getLocation() {
            return location;
        }

        public int getTotalGrievances() {
            return totalGrievances;
        }

        public int getPending() {
            return pending;
        }

        public int getInProgress() {
            return inProgress;
        }

        public int getResolved() {
            return resolved;
        }

        public int getEscalated() {
            return escalated;
        }

        public Map<String, Integer> getCategoryCounts() {
            return categoryCounts;
        }

        public Map<String, Integer> getPriorityCounts() {
            return priorityCounts;
        }

        public String getMessage() {
            return message;
        }
    }
}
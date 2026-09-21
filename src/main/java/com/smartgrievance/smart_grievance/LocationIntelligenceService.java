package com.smartgrievance.smart_grievance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class LocationIntelligenceService {

    private static final int HOTSPOT_THRESHOLD = 3;

    private final GrievanceRepository grievanceRepository;

    public LocationIntelligenceService(
            GrievanceRepository grievanceRepository) {

        this.grievanceRepository = grievanceRepository;
    }

    public LocationInsight analyzeLocation(String location) {

        if (location == null || location.trim().isEmpty()) {

            return new LocationInsight(
                    "",
                    0,
                    false,
                    new LinkedHashMap<>(),
                    "Location information is not available."
            );
        }

        List<Grievance> grievances =
                grievanceRepository.findByLocationIgnoreCase(
                        location.trim()
                );

        Map<String, Integer> categoryCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String category = grievance.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Unclassified";
            }

            categoryCounts.put(
                    category,
                    categoryCounts.getOrDefault(category, 0) + 1
            );
        }

        int totalGrievances = grievances.size();

        boolean hotspot =
                totalGrievances >= HOTSPOT_THRESHOLD;

        String message;

        if (hotspot) {

            message =
                    "This location is showing a high concentration "
                    + "of reported grievances.";
        } else {

            message =
                    "This location has not reached the hotspot threshold.";
        }

        return new LocationInsight(
                location.trim(),
                totalGrievances,
                hotspot,
                categoryCounts,
                message
        );
    }

    public static class LocationInsight {

        private String location;
        private int totalGrievances;
        private boolean hotspot;
        private Map<String, Integer> categoryCounts;
        private String message;

        public LocationInsight(
                String location,
                int totalGrievances,
                boolean hotspot,
                Map<String, Integer> categoryCounts,
                String message) {

            this.location = location;
            this.totalGrievances = totalGrievances;
            this.hotspot = hotspot;
            this.categoryCounts = categoryCounts;
            this.message = message;
        }

        public String getLocation() {
            return location;
        }

        public int getTotalGrievances() {
            return totalGrievances;
        }

        public boolean isHotspot() {
            return hotspot;
        }

        public Map<String, Integer> getCategoryCounts() {
            return categoryCounts;
        }

        public String getMessage() {
            return message;
        }
    }
}
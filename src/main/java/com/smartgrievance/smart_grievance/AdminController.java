package com.smartgrievance.smart_grievance;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final GrievanceRepository grievanceRepository;

    public AdminController(GrievanceRepository grievanceRepository) {
        this.grievanceRepository = grievanceRepository;
    }

    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminStats() {

        List<Grievance> grievances =
                grievanceRepository.findAll();

        Map<String, Object> stats = new HashMap<>();

        // =====================================================
        // OVERALL STATISTICS
        // =====================================================

        long total = grievances.size();

        long pending = grievances.stream()
                .filter(g -> "Pending".equalsIgnoreCase(g.getStatus()))
                .count();

        long inProgress = grievances.stream()
                .filter(g -> "In Progress".equalsIgnoreCase(g.getStatus()))
                .count();

        long resolved = grievances.stream()
                .filter(g -> "Resolved".equalsIgnoreCase(g.getStatus()))
                .count();

        long escalated = grievances.stream()
                .filter(g -> "Escalated".equalsIgnoreCase(
                        g.getEscalationStatus()))
                .count();

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("inProgress", inProgress);
        stats.put("resolved", resolved);
        stats.put("escalated", escalated);

        // =====================================================
        // DEPARTMENT-WISE ANALYTICS
        // =====================================================

        Map<String, Long> departmentCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String department = grievance.getDepartment();

            if (department == null ||
                    department.trim().isEmpty()) {

                department = "Unassigned";
            }

            departmentCounts.put(
                    department,
                    departmentCounts.getOrDefault(
                            department, 0L) + 1
            );
        }

        stats.put(
                "departmentCounts",
                departmentCounts
        );

        // =====================================================
        // CATEGORY-WISE ANALYTICS
        // =====================================================

        Map<String, Long> categoryCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String category = grievance.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Unclassified";
            }

            categoryCounts.put(
                    category,
                    categoryCounts.getOrDefault(
                            category, 0L) + 1
            );
        }

        stats.put(
                "categoryCounts",
                categoryCounts
        );

        // =====================================================
        // DEPARTMENT × CATEGORY ANALYTICS
        // =====================================================

        Map<String, Map<String, Long>>
                departmentCategoryCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String department = grievance.getDepartment();

            if (department == null ||
                    department.trim().isEmpty()) {

                department = "Unassigned";
            }

            String category = grievance.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Unclassified";
            }

            departmentCategoryCounts
                    .computeIfAbsent(
                            department,
                            key -> new LinkedHashMap<>()
                    )
                    .put(
                            category,
                            departmentCategoryCounts
                                    .get(department)
                                    .getOrDefault(
                                            category, 0L
                                    ) + 1
                    );
        }

        stats.put(
                "departmentCategoryCounts",
                departmentCategoryCounts
        );

        // =====================================================
        // CIVIC HOTSPOT / LOCATION INTELLIGENCE
        // =====================================================

        Map<String, Map<String, Object>>
                locationAnalytics =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String location = grievance.getLocation();

            if (location == null ||
                    location.trim().isEmpty()) {

                location = "Unknown";
            }

            Map<String, Object> locationData =
                    locationAnalytics.computeIfAbsent(
                            location,
                            key -> {

                                Map<String, Object> data =
                                        new LinkedHashMap<>();

                                data.put("location", key);
                                data.put("totalGrievances", 0L);
                                data.put("hotspot", false);
                                data.put(
                                        "categoryCounts",
                                        new LinkedHashMap<String, Long>()
                                );

                                return data;
                            }
                    );

            long currentCount =
                    (Long) locationData.get(
                            "totalGrievances"
                    );

            locationData.put(
                    "totalGrievances",
                    currentCount + 1
            );

            @SuppressWarnings("unchecked")
            Map<String, Long> locationCategories =
                    (Map<String, Long>)
                            locationData.get(
                                    "categoryCounts"
                            );

            String category =
                    grievance.getCategory();

            if (category == null ||
                    category.trim().isEmpty()) {

                category = "Unclassified";
            }

            locationCategories.put(
                    category,
                    locationCategories.getOrDefault(
                            category, 0L
                    ) + 1
            );
        }

        // A location becomes a civic hotspot
        // when 3 or more grievances are reported there.

        for (Map<String, Object> locationData :
                locationAnalytics.values()) {

            long count =
                    (Long) locationData.get(
                            "totalGrievances"
                    );

            locationData.put(
                    "hotspot",
                    count >= 3
            );

            if (count >= 3) {

                locationData.put(
                        "message",
                        "This location is showing a high concentration of reported grievances."
                );

            } else {

                locationData.put(
                        "message",
                        "No significant hotspot detected at this location."
                );
            }
        }

        stats.put(
                "locationAnalytics",
                locationAnalytics
        );

        // =====================================================
        // HOTSPOT SUMMARY
        // =====================================================

        long hotspotCount =
                locationAnalytics.values()
                        .stream()
                        .filter(data ->
                                Boolean.TRUE.equals(
                                        data.get("hotspot")
                                ))
                        .count();

        stats.put(
                "hotspotCount",
                hotspotCount
        );

        stats.put(
                "totalLocations",
                locationAnalytics.size()
        );

        return stats;
    }
}
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

    public AdminController(
            GrievanceRepository grievanceRepository) {

        this.grievanceRepository = grievanceRepository;
    }

    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminStats() {

        List<Grievance> grievances =
                grievanceRepository.findAll();

        Map<String, Object> stats =
                new HashMap<>();

        long total =
                grievances.size();

        long pending =
                grievances.stream()
                        .filter(g ->
                                "Pending".equalsIgnoreCase(
                                        g.getStatus()))
                        .count();

        long inProgress =
                grievances.stream()
                        .filter(g ->
                                "In Progress".equalsIgnoreCase(
                                        g.getStatus()))
                        .count();

        long resolved =
                grievances.stream()
                        .filter(g ->
                                "Resolved".equalsIgnoreCase(
                                        g.getStatus()))
                        .count();

        long escalated =
                grievances.stream()
                        .filter(g ->
                                "Escalated".equalsIgnoreCase(
                                        g.getEscalationStatus()))
                        .count();

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("inProgress", inProgress);
        stats.put("resolved", resolved);
        stats.put("escalated", escalated);

        /*
         * Department-wise grievance count
         */
        Map<String, Long> departmentCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String department =
                    grievance.getDepartment();

            if (department == null
                    || department.trim().isEmpty()) {

                department =
                        "Unassigned";
            }

            departmentCounts.put(
                    department,
                    departmentCounts.getOrDefault(
                            department,
                            0L
                    ) + 1
            );
        }

        /*
         * Category-wise grievance count
         */
        Map<String, Long> categoryCounts =
                new LinkedHashMap<>();

        for (Grievance grievance : grievances) {

            String category =
                    grievance.getCategory();

            if (category == null
                    || category.trim().isEmpty()) {

                category =
                        "Unclassified";
            }

            categoryCounts.put(
                    category,
                    categoryCounts.getOrDefault(
                            category,
                            0L
                    ) + 1
            );
        }

        stats.put(
                "departmentCounts",
                departmentCounts
        );

        stats.put(
                "categoryCounts",
                categoryCounts
        );

        return stats;
    }
}
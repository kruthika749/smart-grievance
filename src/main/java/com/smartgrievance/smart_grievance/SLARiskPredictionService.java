package com.smartgrievance.smart_grievance;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class SLARiskPredictionService {

    public SLARiskResult predictRisk(Grievance grievance) {

        if (grievance == null) {

            return new SLARiskResult(
                    "UNKNOWN",
                    0,
                    "Grievance information is not available."
            );
        }

        if ("Resolved".equalsIgnoreCase(
                grievance.getStatus())) {

            return new SLARiskResult(
                    "NO RISK",
                    0,
                    "The grievance has already been resolved."
            );
        }

        if (grievance.getSlaDeadline() == null) {

            return new SLARiskResult(
                    "UNKNOWN",
                    0,
                    "SLA deadline is not available."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime deadline =
                grievance.getSlaDeadline();

        long remainingHours =
                Duration.between(
                        now,
                        deadline
                ).toHours();

        int riskScore = 0;

        String priority =
                grievance.getPriority();

        if ("CRITICAL".equalsIgnoreCase(priority)) {
            riskScore += 30;
        } else if ("HIGH".equalsIgnoreCase(priority)) {
            riskScore += 20;
        } else if ("MEDIUM".equalsIgnoreCase(priority)) {
            riskScore += 10;
        }

        if (remainingHours <= 4) {
            riskScore += 50;
        } else if (remainingHours <= 12) {
            riskScore += 40;
        } else if (remainingHours <= 24) {
            riskScore += 25;
        } else if (remainingHours <= 48) {
            riskScore += 10;
        }

        if ("Pending".equalsIgnoreCase(
                grievance.getStatus())) {

            riskScore += 20;
        }

        if (grievance.getAfterEvidence() == null ||
                grievance.getAfterEvidence()
                        .trim()
                        .isEmpty()) {

            riskScore += 10;
        }

        if (riskScore > 100) {
            riskScore = 100;
        }

        String riskLevel;

        if (riskScore >= 70) {

            riskLevel = "HIGH";

        } else if (riskScore >= 40) {

            riskLevel = "MEDIUM";

        } else {

            riskLevel = "LOW";
        }

        String message =
                buildMessage(
                        riskLevel,
                        remainingHours,
                        grievance
                );

        return new SLARiskResult(
                riskLevel,
                riskScore,
                message
        );
    }

    private String buildMessage(
            String riskLevel,
            long remainingHours,
            Grievance grievance) {

        if ("HIGH".equals(riskLevel)) {

            return "This grievance is at high risk of "
                    + "missing its SLA deadline. "
                    + "Immediate departmental attention is recommended.";
        }

        if ("MEDIUM".equals(riskLevel)) {

            return "This grievance shows moderate SLA risk. "
                    + "The department should prioritize progress "
                    + "before the deadline.";
        }

        return "This grievance currently has low SLA risk "
                + "and sufficient time remains for resolution.";
    }

    public static class SLARiskResult {

        private String riskLevel;
        private int riskScore;
        private String message;

        public SLARiskResult(
                String riskLevel,
                int riskScore,
                String message) {

            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.message = message;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public int getRiskScore() {
            return riskScore;
        }

        public String getMessage() {
            return message;
        }
    }
}
package com.smartgrievance.smart_grievance;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SlaEscalationService {

    private final GrievanceRepository grievanceRepository;

    public SlaEscalationService(
            GrievanceRepository grievanceRepository) {

        this.grievanceRepository =
                grievanceRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void checkSlaBreaches() {

        List<Grievance> grievances =
                grievanceRepository.findAll();

        LocalDateTime now =
                LocalDateTime.now();

        for (Grievance grievance : grievances) {

            if (grievance.getSlaDeadline() == null) {
                continue;
            }

            if ("Resolved".equalsIgnoreCase(
                    grievance.getStatus())) {
                continue;
            }

            if ("Escalated".equalsIgnoreCase(
                    grievance.getEscalationStatus())) {
                continue;
            }

            LocalDateTime deadline =
                    grievance.getSlaDeadline();

            long remainingHours =
                    Duration.between(
                            now,
                            deadline
                    ).toHours();

            String priority =
                    grievance.getPriority();

            boolean shouldEscalate = false;

            /*
             * CRITICAL grievances:
             * Escalate when the SLA is breached
             * or only a small amount of time remains.
             */
            if ("CRITICAL".equalsIgnoreCase(priority)
                    && remainingHours <= 4) {

                shouldEscalate = true;
            }

            /*
             * HIGH priority grievances:
             * Escalate when the SLA is breached
             * or less than 12 hours remain.
             */
            else if ("HIGH".equalsIgnoreCase(priority)
                    && remainingHours <= 12) {

                shouldEscalate = true;
            }

            /*
             * MEDIUM priority grievances:
             * Escalate when the SLA has been breached.
             */
            else if ("MEDIUM".equalsIgnoreCase(priority)
                    && remainingHours < 0) {

                shouldEscalate = true;
            }

            /*
             * LOW priority grievances:
             * Escalate only after SLA breach.
             */
            else if ("LOW".equalsIgnoreCase(priority)
                    && remainingHours < 0) {

                shouldEscalate = true;
            }

            if (shouldEscalate) {

                grievance.setEscalationStatus(
                        "Escalated"
                );

                grievanceRepository.save(grievance);

                System.out.println(
                        "SMART ESCALATION - Grievance ID: "
                        + grievance.getId()
                        + " | Priority: "
                        + priority
                        + " | Remaining Hours: "
                        + remainingHours
                        + " | Escalated"
                );
            }
        }
    }
}
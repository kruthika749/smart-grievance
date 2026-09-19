package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SlaEscalationService {

    private final GrievanceRepository grievanceRepository;

    public SlaEscalationService(GrievanceRepository grievanceRepository) {
        this.grievanceRepository = grievanceRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void checkSlaBreaches() {

        List<Grievance> grievances = grievanceRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Grievance grievance : grievances) {

            if (grievance.getSlaDeadline() != null
                    && grievance.getSlaDeadline().isBefore(now)
                    && !"Resolved".equalsIgnoreCase(grievance.getStatus())
                    && !"Escalated".equalsIgnoreCase(grievance.getEscalationStatus())) {

                grievance.setEscalationStatus("Escalated");

                grievanceRepository.save(grievance);

                System.out.println(
                    "SLA BREACHED - Grievance ID: "
                    + grievance.getId()
                    + " | Escalated"
                );
            }
        }
    }
}
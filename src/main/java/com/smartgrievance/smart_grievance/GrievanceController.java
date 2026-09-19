package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrievanceController {

    private final GrievanceRepository grievanceRepository;

    public GrievanceController(GrievanceRepository grievanceRepository) {
        this.grievanceRepository = grievanceRepository;
    }

    // Testing endpoint to simulate an SLA breach
    @GetMapping("/test/escalate/{id}")
    public String forceSlaBreach(@PathVariable Integer id) {

        Grievance grievance =
                grievanceRepository.findById(id).orElse(null);

        if (grievance == null) {
            return "Grievance not found";
        }

        grievance.setSlaDeadline(
                LocalDateTime.now().minusHours(1)
        );

        grievanceRepository.save(grievance);

        return "SLA deadline expired for grievance ID: " + id;
    }
}
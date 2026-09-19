package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrievanceController {

    private final GrievanceRepository grievanceRepository;

    public GrievanceController(GrievanceRepository grievanceRepository) {
        this.grievanceRepository = grievanceRepository;
    }

    @GetMapping("/grievances")
    public List<Grievance> getAllGrievances() {
        return grievanceRepository.findAll();
    }

    @GetMapping("/grievances/location/{location}")
    public List<Grievance> getGrievancesByLocation(
            @PathVariable String location) {

        return grievanceRepository.findByLocationIgnoreCase(location);
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
package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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
}
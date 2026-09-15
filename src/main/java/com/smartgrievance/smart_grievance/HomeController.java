package com.smartgrievance.smart_grievance;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final GrievanceRepository grievanceRepository;

    public HomeController(GrievanceRepository grievanceRepository) {
        this.grievanceRepository = grievanceRepository;
    }

    // Submit a new grievance
    @PostMapping("/submit")
    public String submitGrievance(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String grievance,
            @RequestParam String location) {

        Grievance g = new Grievance();

        g.setName(name);
        g.setEmail(email);
        g.setGrievance(grievance);
        g.setLocation(location);
        g.setStatus("PENDING");

        grievanceRepository.save(g);

        return "Grievance submitted successfully!<br><br>"
                + "Name: " + name + "<br>"
                + "Email: " + email + "<br>"
                + "Grievance: " + grievance + "<br>"
                + "Location: " + location
                + "<br>Status: PENDING";
    }

    // View all grievances
    @GetMapping("/grievances")
    public List<Grievance> getAllGrievances() {
        return grievanceRepository.findAll();
    }

    // View one grievance by ID
    @GetMapping("/grievances/{id}")
    public Optional<Grievance> getGrievanceById(@PathVariable int id) {
        return grievanceRepository.findById(id);
    }

    // Search grievances by location
    @GetMapping("/grievances/location/{location}")
    public List<Grievance> getGrievancesByLocation(
            @PathVariable String location) {

        return grievanceRepository.findByLocationIgnoreCase(location);
    }

    // Filter grievances by status
    @GetMapping("/grievances/status/{status}")
    public List<Grievance> getGrievancesByStatus(
            @PathVariable String status) {

        return grievanceRepository.findByStatusIgnoreCase(status);
    }

    // Update grievance status
    @PutMapping("/grievances/{id}/status")
    public String updateStatus(
            @PathVariable int id,
            @RequestParam String status) {

        Optional<Grievance> optionalGrievance =
                grievanceRepository.findById(id);

        if (optionalGrievance.isEmpty()) {
            return "Grievance not found with ID: " + id;
        }

        Grievance grievance = optionalGrievance.get();
        grievance.setStatus(status.toUpperCase());

        grievanceRepository.save(grievance);

        return "Grievance status updated successfully to: "
                + status.toUpperCase();
    }
}

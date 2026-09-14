package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping("/submit")
    public String submitGrievance(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String grievance,
            @RequestParam String location,
            @RequestParam(defaultValue = "Medium") String priority) {

        Grievance g = new Grievance();

        g.setName(name);
        g.setEmail(email);
        g.setGrievance(grievance);
        g.setLocation(location);
        g.setStatus("Pending");

        g.setPriority(priority);

        if ("High".equalsIgnoreCase(g.getPriority())) {
            g.setDeadline(LocalDateTime.now().plusHours(24));
        } else if ("Low".equalsIgnoreCase(g.getPriority())) {
            g.setDeadline(LocalDateTime.now().plusHours(72));
        } else {
            g.setDeadline(LocalDateTime.now().plusHours(48));
        }

        grievanceRepository.save(g);

        return "Grievance submitted successfully!<br><br>"
                + "Name: " + name + "<br>"
                + "Email: " + email + "<br>"
                + "Grievance: " + grievance + "<br>"
                + "Location: " + location;
    }

    @GetMapping("/grievances")
    public List<Grievance> getGrievances() {
        return grievanceRepository.findAll();
    }

    @PutMapping("/grievances/{id}/status")
    public String updateStatus(
            @PathVariable int id,
            @RequestParam String status) {

        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        if ("Resolved".equalsIgnoreCase(status)) {
            grievance.setResolvedAt(LocalDateTime.now());
        } else {
            grievance.setResolvedAt(null);
        }

        grievance.setStatus(status);
        grievanceRepository.save(grievance);

        return "Status updated successfully";
    }

    @PutMapping("/grievances/{id}/verify")
    public String verifyGrievance(
            @PathVariable int id,
            @RequestParam String verificationStatus,
            @RequestParam(required = false) String verificationComment) {

        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        grievance.setVerificationStatus(verificationStatus);
        grievance.setVerificationComment(verificationComment);
        grievance.setVerifiedAt(LocalDateTime.now());

        grievanceRepository.save(grievance);

        return "Grievance verification recorded successfully";
    }
}

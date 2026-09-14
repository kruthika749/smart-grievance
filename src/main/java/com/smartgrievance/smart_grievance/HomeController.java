package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;



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
            @RequestParam String location) {

        Grievance g = new Grievance();

        g.setName(name);
        g.setEmail(email);
        g.setGrievance(grievance);
        g.setLocation(location);
        g.setStatus("Pending");

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
}

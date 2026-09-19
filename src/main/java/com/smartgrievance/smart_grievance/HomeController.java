package com.smartgrievance.smart_grievance;

import com.smartgrievance.smart_grievance.ml.CategoryService;
import com.smartgrievance.smart_grievance.ml.PriorityPredictor;

import java.time.LocalDateTime;
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
    private final CategoryService categoryService;
    private final DuplicateGrievanceService duplicateGrievanceService;

    public HomeController(
            GrievanceRepository grievanceRepository,
            CategoryService categoryService,
            DuplicateGrievanceService duplicateGrievanceService) {

        this.grievanceRepository = grievanceRepository;
        this.categoryService = categoryService;
        this.duplicateGrievanceService = duplicateGrievanceService;
    }

    // Submit a new grievance
    @PostMapping("/submit")
    public String submitGrievance(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String grievance,
            @RequestParam String location,
            @RequestParam(defaultValue = "Medium") String priority) {

        // AI Category Prediction
        String category =
                categoryService.predictCategory(grievance);

        // Duplicate / Similar Grievance Detection
        Grievance similarGrievance =
                duplicateGrievanceService.findSimilarGrievance(
                        grievance,
                        location,
                        category
                );

        // AI Priority Prediction
        String predictedPriority =
                PriorityPredictor.predictPriority(grievance);

        // Automatic Department Routing
        String department =
                getDepartment(category);

        // Calculate SLA hours based on AI priority
        int slaHours =
                getSlaHours(predictedPriority);

        // Calculate SLA deadline
        LocalDateTime slaDeadline =
                LocalDateTime.now().plusHours(slaHours);

        // Create grievance
        Grievance g = new Grievance();

        g.setName(name);
        g.setEmail(email);
        g.setGrievance(grievance);
        g.setLocation(location);
        g.setStatus("Pending");

        // AI and routing results
        g.setCategory(category);
        g.setPriority(predictedPriority);
        g.setDepartment(department);

        // SLA information
        g.setSlaHours(slaHours);
        g.setSlaDeadline(slaDeadline);

        // Database branch deadline field
        g.setDeadline(slaDeadline);

        // Initialize escalation
        g.setEscalationStatus("Not Escalated");

        // Verification starts as pending
        g.setVerificationStatus("Pending");

        // Save to MySQL
        grievanceRepository.save(g);

        // Prepare duplicate information
        String duplicateMessage;

        if (similarGrievance != null) {

            duplicateMessage =
                    "<div style='margin-top:20px;padding:15px;"
                    + "background:#fff4d6;border-radius:10px;'>"
                    + "<strong>⚠ Similar grievance detected</strong><br><br>"
                    + "Existing Grievance ID: #"
                    + similarGrievance.getId()
                    + "<br>"
                    + "Category: "
                    + similarGrievance.getCategory()
                    + "<br>"
                    + "Location: "
                    + similarGrievance.getLocation()
                    + "<br>"
                    + "Status: "
                    + similarGrievance.getStatus()
                    + "</div>";

        } else {

            duplicateMessage =
                    "<div style='margin-top:20px;padding:15px;"
                    + "background:#e8f7ee;border-radius:10px;'>"
                    + "<strong>✓ No similar grievance detected</strong>"
                    + "</div>";
        }

        return "Grievance submitted successfully!<br><br>"
                + "Name: " + name + "<br>"
                + "Email: " + email + "<br>"
                + "Grievance: " + grievance + "<br>"
                + "Location: " + location + "<br>"
                + "AI Category: " + category + "<br>"
                + "AI Priority: " + predictedPriority + "<br>"
                + "Department: " + department + "<br>"
                + "SLA: " + slaHours + " hours<br>"
                + "SLA Deadline: " + slaDeadline + "<br>"
                + "Escalation: Not Escalated<br>"
                + "Status: Pending"
                + duplicateMessage;
    }

    // Assign SLA hours based on priority
    private int getSlaHours(String priority) {

        switch (priority) {

            case "CRITICAL":
                return 4;

            case "HIGH":
                return 24;

            case "MEDIUM":
                return 48;

            case "LOW":
                return 72;

            default:
                return 48;
        }
    }

    // Automatically assign department based on AI category
    private String getDepartment(String category) {

        switch (category) {

            case "ROAD":
                return "Roads & Infrastructure";

            case "WATER":
                return "Water Supply";

            case "ELECTRICITY":
                return "Electricity Department";

            case "WASTE":
                return "Solid Waste Management";

            case "DRAINAGE":
                return "Drainage & Sewage";

            case "STREETLIGHT":
                return "Streetlight Department";

            case "PUBLIC_SAFETY":
                return "Public Safety Department";

            case "HEALTHCARE":
                return "Health Department";

            case "EDUCATION":
                return "Education Department";

            case "PUBLIC_TRANSPORT":
                return "Public Transport";

            case "TRAFFIC":
                return "Traffic Management";

            case "PUBLIC_TOILETS":
                return "Public Toilet Maintenance";

            case "PARKS":
                return "Parks & Horticulture";

            case "ANIMAL_CONTROL":
                return "Animal Control";

            case "ENVIRONMENT":
                return "Environment Department";

            case "HOUSING":
                return "Housing Department";

            case "DISABILITY_ACCESS":
                return "Accessibility Services";

            case "GOVERNMENT_SERVICES":
                return "Municipal Services";

            case "OTHER":
                return "General Grievance Cell";

            default:
                return "General Grievance Cell";
        }
    }

    // View all grievances
    @GetMapping("/grievances")
    public List<Grievance> getAllGrievances() {

        return grievanceRepository.findAll();
    }

    // View one grievance by ID
    @GetMapping("/grievances/{id}")
    public Optional<Grievance> getGrievanceById(
            @PathVariable int id) {

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

        if ("Resolved".equalsIgnoreCase(status)) {
            grievance.setResolvedAt(LocalDateTime.now());
        } else {
            grievance.setResolvedAt(null);
        }

        grievance.setStatus(status);

        grievanceRepository.save(grievance);

        return "Grievance status updated successfully to: "
                + status;
    }

    // Citizen verification
    @PutMapping("/grievances/{id}/verify")
    public String verifyGrievance(
            @PathVariable int id,
            @RequestParam String verificationStatus,
            @RequestParam(required = false) String verificationComment) {

        Grievance grievance =
                grievanceRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Grievance not found"
                                )
                        );

        grievance.setVerificationStatus(verificationStatus);
        grievance.setVerificationComment(verificationComment);
        grievance.setVerifiedAt(LocalDateTime.now());

        if ("Rejected".equalsIgnoreCase(verificationStatus)) {

            grievance.setStatus("In Progress");
            grievance.setResolvedAt(null);
        }

        grievanceRepository.save(grievance);

        return "Grievance verification recorded successfully";
    }
}
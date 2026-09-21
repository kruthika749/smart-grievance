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
    private final NotificationService notificationService;
    private final CommunityIssueService communityIssueService;
    private final LocationIntelligenceService locationIntelligenceService;
    private final AIResolutionVerificationService
        aiResolutionVerificationService;
    public HomeController(
        GrievanceRepository grievanceRepository,
        CategoryService categoryService,
        DuplicateGrievanceService duplicateGrievanceService,
        NotificationService notificationService,
        CommunityIssueService communityIssueService,
        LocationIntelligenceService locationIntelligenceService,
        AIResolutionVerificationService aiResolutionVerificationService) { 

    this.grievanceRepository =
            grievanceRepository;

    this.categoryService =
            categoryService;

    this.duplicateGrievanceService =
            duplicateGrievanceService;

    this.notificationService =
            notificationService;

    this.communityIssueService =
            communityIssueService;

    this.locationIntelligenceService =
            locationIntelligenceService;

    this.aiResolutionVerificationService =
            aiResolutionVerificationService;
}
 
    @PostMapping("/submit")
    public String submitGrievance(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String grievance,
            @RequestParam String location,
            @RequestParam(defaultValue = "Medium") String priority,
            @RequestParam Integer userId) {

        String category =
                categoryService.predictCategory(grievance);
        String categoryReason =
                categoryService.explainCategory(grievance);

        Grievance similarGrievance =
                duplicateGrievanceService.findSimilarGrievance(
                        grievance,
                        location,
                        category
                );

        String predictedPriority =
                PriorityPredictor.predictPriority(grievance);
        String priorityReason =
                PriorityPredictor.explainPriority(grievance);

        String department =
                getDepartment(category);
        
       
        int slaHours =
                getSlaHours(predictedPriority);

        LocalDateTime slaDeadline =
                LocalDateTime.now().plusHours(slaHours);

        Grievance g = new Grievance();

        g.setName(name);
        g.setEmail(email);
        g.setUserId(userId);
        g.setGrievance(grievance);
        g.setLocation(location);
        g.setStatus("Pending");
        g.setCategory(category);
        g.setCategoryReason(categoryReason);

        g.setPriority(predictedPriority);
        g.setPriorityReason(priorityReason);

        g.setDepartment(department);

        g.setSlaHours(slaHours);
        g.setSlaDeadline(slaDeadline);

        g.setDeadline(slaDeadline);

        g.setEscalationStatus("Not Escalated");

        g.setVerificationStatus("Pending");

        grievanceRepository.save(g);
        CommunityIssueService.CommunityIssueResult communityResult =
        communityIssueService.analyzeCommunityIssue(
                location,
                category
        );

        notificationService.createNotification(
            userId,
            "Grievance Submitted",
            "Your grievance has been submitted successfully. "
                    + "Category: " + category
                    + ", Department: " + department
                    + ", Priority: " + predictedPriority,
            "GRIEVANCE_SUBMITTED"
    );

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
                + "User ID: " + userId + "<br>"
                + "Name: " + name + "<br>"
                + "Email: " + email + "<br>"
                + "Grievance: " + grievance + "<br>"
                + "Location: " + location + "<br>"
                + "AI Category: " + category + "<br>"
                + "Category Reason: " + categoryReason + "<br>"
                + "AI Priority: " + predictedPriority + "<br>"
                + "Priority Reason: " + priorityReason + "<br>"
                + "Department: " + department + "<br>"
                + "SLA: " + slaHours + " hours<br>"
                + "SLA Deadline: " + slaDeadline + "<br>"
                + "Escalation: Not Escalated<br>"
                + "Status: Pending"
                + "<br>Community Issue: "
                + (communityResult.isCommunityIssueDetected()
                ? "YES"
                : "NO")
                + "<br>Related Grievances: "
                + communityResult.getRelatedGrievanceCount()
                + "<br>Community Analysis: "
                + communityResult.getMessage()
                + duplicateMessage;
    }

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

    @GetMapping("/grievances")
    public List<Grievance> getAllGrievances() {

        return grievanceRepository.findAll();
    }

    @GetMapping("/grievances/{id}")
    public Optional<Grievance> getGrievanceById(
            @PathVariable int id) {

        return grievanceRepository.findById(id);
    }

    @GetMapping("/grievances/location/{location}")
    public List<Grievance> getGrievancesByLocation(
            @PathVariable String location) {

        return grievanceRepository.findByLocationIgnoreCase(location);
    }

    @GetMapping("/grievances/status/{status}")
    public List<Grievance> getGrievancesByStatus(
            @PathVariable String status) {

        return grievanceRepository.findByStatusIgnoreCase(status);
    }

    @PutMapping("/grievances/{id}/status")
    public String updateStatus(
            @PathVariable int id,
            @RequestParam String status) {

        Optional<Grievance> optionalGrievance =
                grievanceRepository.findById(id);

        if (optionalGrievance.isEmpty()) {

            return "Grievance not found with ID: " + id;
        }

        Grievance grievance =
                optionalGrievance.get();

                if ("Resolved".equalsIgnoreCase(status)) {

                    if (grievance.getAfterEvidence() == null ||
                            grievance.getAfterEvidence().trim().isEmpty()) {
                
                        return "Cannot mark grievance as Resolved. "
                                + "Resolution evidence is required.";
                    }
                
                    grievance.setResolvedAt(
                            LocalDateTime.now()
                    );
                }else {

            grievance.setResolvedAt(null);
        }

        grievance.setStatus(status);

        grievanceRepository.save(grievance);

        return "Grievance status updated successfully to: "
                + status;
    }
    @PutMapping("/grievances/{id}/resolution-evidence")
public String addResolutionEvidence(
        @PathVariable int id,
        @RequestParam String afterEvidence) {

    Optional<Grievance> optionalGrievance =
            grievanceRepository.findById(id);

    if (optionalGrievance.isEmpty()) {

        return "Grievance not found with ID: " + id;
    }

    Grievance grievance =
            optionalGrievance.get();

    grievance.setAfterEvidence(afterEvidence);

    grievanceRepository.save(grievance);

    return "Resolution evidence added successfully for grievance ID: "
            + id;
}

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

        grievance.setVerificationStatus(
                verificationStatus
        );

        grievance.setVerificationComment(
                verificationComment
        );

        grievance.setVerifiedAt(
                LocalDateTime.now()
        );

        if ("Rejected".equalsIgnoreCase(
                verificationStatus)) {

            grievance.setStatus("In Progress");

            grievance.setResolvedAt(null);
        }

        grievanceRepository.save(grievance);
        return "Grievance verification recorded successfully";
    }

    @GetMapping("/grievances/user/{userId}")
    public List<Grievance> getGrievancesByUser(
            @PathVariable Integer userId) {

        return grievanceRepository.findByUserId(userId);
    }
    @GetMapping("/grievances/department/{department}")
    public List<Grievance> getGrievancesByDepartment(
        @PathVariable String department) {

        return grievanceRepository.findByDepartmentIgnoreCase(department);
}
@GetMapping("/location-intelligence/{location}")
public LocationIntelligenceService.LocationInsight
        getLocationIntelligence(
                @PathVariable String location) {

    return locationIntelligenceService
            .analyzeLocation(location);
}
@PostMapping("/grievances/{id}/ai-verify-resolution")
public AIResolutionVerificationService.VerificationResult
        verifyResolutionWithAI(@PathVariable int id) {

    Optional<Grievance> optionalGrievance =
            grievanceRepository.findById(id);

    if (optionalGrievance.isEmpty()) {

        return new AIResolutionVerificationService.VerificationResult(
                false,
                0,
                "Grievance not found with ID: " + id
        );
    }

    Grievance grievance =
            optionalGrievance.get();

    return aiResolutionVerificationService.verifyResolution(
            grievance.getGrievance(),
            grievance.getAfterEvidence()
    );
}
}
       
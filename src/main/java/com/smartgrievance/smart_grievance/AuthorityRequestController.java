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
public class AuthorityRequestController {

    private final AuthorityRequestRepository repository;

    private final GrievanceRepository grievanceRepository;

    public AuthorityRequestController(
            AuthorityRequestRepository repository,
            GrievanceRepository grievanceRepository) {

        this.repository = repository;
        this.grievanceRepository =
                grievanceRepository;
    }

    /*
     * HIGHER AUTHORITY
     * Request explanation from department
     */
    @PostMapping("/authority/requests")
    public AuthorityRequest requestExplanation(

            @RequestParam Integer grievanceId,

            @RequestParam String requestedBy) {

        Grievance grievance =
                grievanceRepository
                        .findById(grievanceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Grievance not found"));

        AuthorityRequest request =
                new AuthorityRequest();

        request.setGrievanceId(
                grievanceId);

        request.setDepartment(
                grievance.getDepartment());

        request.setRequestedBy(
                requestedBy);

        request.setStatus(
                "EXPLANATION_REQUESTED");

        request.setRequestMessage(
                "Higher Authority has requested an explanation regarding this grievance.");

        return repository.save(request);
    }


    /*
     * HIGHER AUTHORITY
     * View all explanation requests
     */
    @GetMapping("/authority/requests")
    public List<AuthorityRequest>
    getAllRequests() {

        return repository
                .findAllByOrderByRequestedAtDesc();
    }


    /*
     * DEPARTMENT HEAD
     * View requests for department
     */
    @GetMapping(
        "/authority/requests/department/{department}"
    )
    public List<AuthorityRequest>
    getDepartmentRequests(
            @PathVariable String department) {

        return repository
                .findByDepartmentIgnoreCaseOrderByRequestedAtDesc(
                        department);
    }


    /*
     * DEPARTMENT HEAD
     * Submit own explanation
     */
    @PutMapping(
        "/authority/requests/{id}/explanation"
    )
    public AuthorityRequest
    submitExplanation(

            @PathVariable Integer id,

            @RequestParam String explanation) {

        AuthorityRequest request =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authority request not found"));

        request.setDepartmentExplanation(
                explanation);

        request.setStatus(
                "EXPLANATION_SUBMITTED");

        request.setExplanationSubmittedAt(
                LocalDateTime.now());

        return repository.save(request);
    }
}
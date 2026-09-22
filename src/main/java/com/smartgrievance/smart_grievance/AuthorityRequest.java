package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "authority_requests")
public class AuthorityRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer grievanceId;

    private String department;

    private String requestedBy;

    private String status;

    private String requestMessage;

    private String departmentExplanation;

    private LocalDateTime requestedAt;

    private LocalDateTime explanationSubmittedAt;

    public AuthorityRequest() {
    }

    @PrePersist
    public void onCreate() {

        requestedAt = LocalDateTime.now();

        if (status == null || status.isBlank()) {
            status = "EXPLANATION_REQUESTED";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGrievanceId() {
        return grievanceId;
    }

    public void setGrievanceId(Integer grievanceId) {
        this.grievanceId = grievanceId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getDepartmentExplanation() {
        return departmentExplanation;
    }

    public void setDepartmentExplanation(String departmentExplanation) {
        this.departmentExplanation = departmentExplanation;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getExplanationSubmittedAt() {
        return explanationSubmittedAt;
    }

    public void setExplanationSubmittedAt(
            LocalDateTime explanationSubmittedAt) {

        this.explanationSubmittedAt =
                explanationSubmittedAt;
    }
}
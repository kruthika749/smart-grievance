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
public class ReportController {

    private final ReportRepository reportRepository;

    public ReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @PostMapping("/reports")
    public Report createReport(
            @RequestParam Integer userId,
            @RequestParam(required = false) Integer grievanceId,
            @RequestParam String reportType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String officialName,
            @RequestParam String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String amount,
            @RequestParam(required = false) String evidence) {

        Report report = new Report();

        report.setUserId(userId);
        report.setGrievanceId(grievanceId);
        report.setReportType(reportType);
        report.setDepartment(department);
        report.setOfficialName(officialName);
        report.setDescription(description);
        report.setLocation(location);
        report.setAmount(amount);
        report.setEvidence(evidence);
        report.setStatus("SUBMITTED");

        return reportRepository.save(report);
    }

    @GetMapping("/reports/user/{userId}")
    public List<Report> getUserReports(
            @PathVariable Integer userId) {

        return reportRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/authority/reports")
    public List<Report> getAuthorityReports() {

        return reportRepository
                .findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/authority/reports/{id}/status")
    public Report updateReportStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam(required = false) String response) {

        Report report = reportRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Report not found"));

        report.setStatus(status);
        report.setAuthorityResponse(response);
        report.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }
}
package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRequestRepository
        extends JpaRepository<AuthorityRequest, Integer> {

    List<AuthorityRequest>
    findAllByOrderByRequestedAtDesc();

    List<AuthorityRequest>
    findByDepartmentIgnoreCaseOrderByRequestedAtDesc(
            String department);

    List<AuthorityRequest>
    findByGrievanceIdOrderByRequestedAtDesc(
            Integer grievanceId);
}
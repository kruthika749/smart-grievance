package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GrievanceRepository
        extends JpaRepository<Grievance, Integer> {

    List<Grievance> findByLocationIgnoreCase(String location);

    List<Grievance> findByStatusIgnoreCase(String status);

    List<Grievance> findByUserId(Integer userId);

    List<Grievance> findByDepartmentIgnoreCase(String department);

    List<Grievance> findByLocationIgnoreCaseAndCategoryIgnoreCase(
            String location,
            String category
    );
}
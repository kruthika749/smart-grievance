package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    List<Report> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Report> findAllByOrderByCreatedAtDesc();
}
package com.smartgrievance.smart_grievance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Integer> {

    Optional<OtpVerification> findTopByMobileOrderByIdDesc(String mobile);
}
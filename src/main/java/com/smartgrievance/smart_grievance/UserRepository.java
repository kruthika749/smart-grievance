package com.smartgrievance.smart_grievance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);
}
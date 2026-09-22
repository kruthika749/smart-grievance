package com.smartgrievance.smart_grievance;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthorityAccountInitializer {

    @Bean
    CommandLineRunner createAuthorityAccounts(UserRepository userRepository) {

        return args -> {

            BCryptPasswordEncoder passwordEncoder =
                    new BCryptPasswordEncoder();

            // =========================
            // ADMIN ACCOUNT
            // =========================

            if (!userRepository.existsByMobile("9000000001")) {

                User admin = new User();

                admin.setName("System Admin");
                admin.setEmail("admin@smartgrievance.com");
                admin.setMobile("9000000001");

                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );

                admin.setRole("ADMIN");
                admin.setDepartment(null);
                admin.setMobileVerified(true);

                userRepository.save(admin);

                System.out.println(
                        "Admin account created successfully."
                );
            }


            // =========================
            // DEPARTMENT HEAD ACCOUNT
            // =========================

            if (!userRepository.existsByMobile("9000000002")) {

                User departmentHead = new User();

                departmentHead.setName("Streetlight Department Head");
                departmentHead.setEmail(
                        "streetlight.head@smartgrievance.com"
                );
                departmentHead.setMobile("9000000002");

                departmentHead.setPassword(
                        passwordEncoder.encode("Dept@123")
                );

                departmentHead.setRole("DEPARTMENT_HEAD");
                departmentHead.setDepartment(
                        "Streetlight Department"
                );
                departmentHead.setMobileVerified(true);

                userRepository.save(departmentHead);

                System.out.println(
                        "Department Head account created successfully."
                );
            }


            // =========================
            // HIGHER AUTHORITY ACCOUNT
            // =========================

            if (!userRepository.existsByMobile("9000000003")) {

                User higherAuthority = new User();

                higherAuthority.setName("Higher Authority");
                higherAuthority.setEmail(
                        "authority@smartgrievance.com"
                );
                higherAuthority.setMobile("9000000003");

                higherAuthority.setPassword(
                        passwordEncoder.encode("Authority@123")
                );

                higherAuthority.setRole("HIGHER_AUTHORITY");
                higherAuthority.setDepartment(null);
                higherAuthority.setMobileVerified(true);

                userRepository.save(higherAuthority);

                System.out.println(
                        "Higher Authority account created successfully."
                );
            }
        };
    }
}
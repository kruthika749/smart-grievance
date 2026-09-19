package com.smartgrievance.smart_grievance;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;

    public UserService(
            UserRepository userRepository,
            OtpVerificationRepository otpRepository) {

        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
    }

    public String registerCitizen(
            String name,
            String email,
            String mobile,
            String password) {

        Optional<OtpVerification> otp =
                otpRepository.findTopByMobileOrderByIdDesc(mobile);

        if (otp.isEmpty() || !otp.get().isVerified()) {
            return "Mobile number is not verified. Please verify OTP first.";
        }

        if (userRepository.existsByMobile(mobile)) {
            return "Mobile number is already registered.";
        }

        if (userRepository.existsByEmail(email)) {
            return "Email is already registered.";
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(password);
        user.setRole("CITIZEN");
        user.setMobileVerified(true);

        userRepository.save(user);

        return "Citizen account created successfully.";
    }
}
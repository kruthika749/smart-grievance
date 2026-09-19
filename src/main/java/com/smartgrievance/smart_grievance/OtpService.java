package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;

    private final Random random = new Random();

    public OtpService(OtpVerificationRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public String generateOtp(String mobile) {

        String otp =
                String.format("%06d", random.nextInt(1000000));

        OtpVerification verification =
                new OtpVerification();

        verification.setMobile(mobile);
        verification.setOtp(otp);

        verification.setExpiryTime(
                LocalDateTime.now().plusMinutes(5)
        );

        verification.setAttempts(0);
        verification.setVerified(false);

        otpRepository.save(verification);

        return otp;
    }

    public String verifyOtp(String mobile, String enteredOtp) {

        Optional<OtpVerification> optionalOtp =
                otpRepository
                        .findTopByMobileOrderByIdDesc(mobile);

        if (optionalOtp.isEmpty()) {
            return "No OTP found. Please request a new OTP.";
        }

        OtpVerification verification =
                optionalOtp.get();

        if (verification.isVerified()) {
            return "OTP already verified.";
        }

        if (LocalDateTime.now()
                .isAfter(verification.getExpiryTime())) {

            return "OTP expired. Please request a new OTP.";
        }

        if (verification.getAttempts() >= 5) {
            return "Too many incorrect attempts. Please request a new OTP.";
        }

        if (!verification.getOtp().equals(enteredOtp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            return "Incorrect OTP. Attempts remaining: "
                    + (5 - verification.getAttempts());
        }

        verification.setVerified(true);

        otpRepository.save(verification);

        return "OTP verified successfully.";
    }
}
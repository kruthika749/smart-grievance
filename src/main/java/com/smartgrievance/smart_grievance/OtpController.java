package com.smartgrievance.smart_grievance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping("/send-otp")
    public String sendOtp(
            @RequestParam String mobile) {

        String otp = otpService.generateOtp(mobile);

        return "OTP generated successfully. "
                + "Demo OTP: " + otp;
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String mobile,
            @RequestParam String otp) {

        return otpService.verifyOtp(mobile, otp);
    }
}
package com.smartgrievance.smart_grievance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestParam String mobile,
            @RequestParam String password) {

        Optional<User> optionalUser =
                userRepository.findByMobile(mobile);

        Map<String, Object> response =
                new HashMap<>();

        if (optionalUser.isEmpty()) {

            response.put("success", false);
            response.put(
                    "message",
                    "Invalid mobile number or password."
            );

            return response;
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            response.put("success", false);
            response.put(
                    "message",
                    "Invalid mobile number or password."
            );

            return response;
        }

        response.put("success", true);
        response.put(
                "message",
                "Login successful!"
        );
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return response;
    }
}
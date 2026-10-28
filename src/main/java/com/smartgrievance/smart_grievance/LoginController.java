
package com.smartgrievance.smart_grievance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
            @RequestParam String password,
            HttpServletRequest request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Optional<User> optionalUser =
                userRepository.findByMobile(mobile);

        // Check whether the user exists.
        if (optionalUser.isEmpty()) {
            response.put("success", false);
            response.put(
                    "message",
                    "Invalid mobile number or password."
            );
            return response;
        }

        User user = optionalUser.get();

        // Verify the submitted password against the stored BCrypt hash.
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

        // Prevent session fixation.
        // Change the existing session ID after successful authentication.
        request.changeSessionId();

        // Store verified user details in the server-side session.
        session.setAttribute("loggedInUserId", user.getId());
        session.setAttribute("loggedInUserName", user.getName());
        session.setAttribute("loggedInUserRole", user.getRole());

        // Preserve the response expected by login.html.
        response.put("success", true);
        response.put("message", "Login successful!");
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return response;
    }
}

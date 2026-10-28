
package com.smartgrievance.smart_grievance.chatbot;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public Map<String, Object> sendMessage(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        String message = request.get("message");

        if (message == null || message.isBlank()) {
            result.put("response", "Please enter a message.");
            result.put("loggedIn", session.getAttribute("loggedInUserId") != null);
            return result;
        }

        Integer userId =
                (Integer) session.getAttribute("loggedInUserId");

        String userName =
                (String) session.getAttribute("loggedInUserName");

        String response = chatbotService.getResponse(message);

        result.put("response", response);
        result.put("loggedIn", userId != null);

        if (userId != null) {
            result.put("userId", userId);
            result.put("userName", userName);
        }

        return result;
    }
}

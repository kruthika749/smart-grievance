
package com.smartgrievance.smart_grievance.chatbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.smartgrievance.smart_grievance.ml.CategoryService;

@Service
public class ChatbotService {

    private final CategoryService categoryService;

    public ChatbotService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public String getResponse(String message) {

        if (message == null || message.isBlank()) {
            return "Please type a message. I'm here to help!";
        }

        String text = message.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[.!?]+$", "")
                .replaceAll("\\s+", " ");

        // 1. Greetings
        if (matches(text, "hi", "hello", "hey", "heya",
                "good morning", "good afternoon", "good evening")) {
            return "Hello! Welcome to SmartGrievance. "
                    + "I'm your virtual assistant. "
                    + "What can I help you with today?";
        }

        // 2. Well-being
        if (text.contains("how are you")) {
            return "I'm doing well, thank you! I'm ready to help "
                    + "you with SmartGrievance.";
        }

        // 3. Identity
        if (containsAny(text, "who are you", "your name",
                "what are you")) {
            return "I'm the SmartGrievance Assistant. I help citizens "
                    + "understand the platform, get guidance about "
                    + "civic problems and classify grievances.";
        }

        // 4. Capabilities
        if (containsAny(text, "what can you do",
                "how can you help", "your features",
                "help me", "what do you do")) {
            return "I can help with:\n"
                    + "• Website and account guidance\n"
                    + "• Complaint-writing instructions\n"
                    + "• Civic issue classification\n"
                    + "• Common residential problems\n"
                    + "• Parking and traffic guidance\n"
                    + "• Priority assessment\n"
                    + "• Complaint submission guidance\n"
                    + "• Grievance tracking guidance\n\n"
                    + "Tell me what you need help with!";
        }

        // 5. About SmartGrievance
        if (containsAny(text, "what is smartgrievance",
                "about smartgrievance", "purpose of this website",
                "about this website", "what is this website")) {
            return "SmartGrievance is a civic grievance management "
                    + "platform designed to help citizens report "
                    + "public issues. Its AI component helps classify "
                    + "reported civic problems.";
        }

        // 6. Thanks
        if (containsAny(text, "thank you", "thanks",
                "thank u", "thx")) {
            return "You're very welcome! I'm happy to help. "
                    + "Feel free to ask me about SmartGrievance.";
        }

        // 7. Apologies
        if (containsAny(text, "sorry", "my apologies")) {
            return "No worries! Please tell me what you need, "
                    + "and I'll do my best to guide you.";
        }

        // 8. Goodbye
        if (matches(text, "bye", "goodbye", "see you",
                "see you later", "good night")) {
            return "Goodbye! Thank you for using SmartGrievance. "
                    + "Have a wonderful day!";
        }

        // 9. Registration
        if (containsAny(text, "register", "registration",
                "create an account", "sign up", "new account")) {
            return "To register:\n"
                    + "1. Open the SmartGrievance homepage.\n"
                    + "2. Select Register.\n"
                    + "3. Enter the required information.\n"
                    + "4. Follow the displayed instructions.\n\n"
                    + "Use the official website for personal information.";
        }

        // 10. Password recovery
        if (containsAny(text, "forgot password",
                "reset password", "password problem",
                "forgot my password")) {
            return "Check the login page for a password recovery "
                    + "option. If one is unavailable, contact your "
                    + "SmartGrievance administrator. Never share "
                    + "your password or OTP in this chat.";
        }

        // 11. Login
        if (containsAny(text, "login", "log in", "sign in",
                "cannot log in", "can't log in",
                "login problem")) {
            return "Select Login on the homepage and enter your "
                    + "registered credentials. Check your details "
                    + "if login fails and use the available recovery "
                    + "option if provided.";
        }

        // 12. Complaint submission instructions
        if (containsAny(text, "submit a complaint",
                "submit a grievance", "raise a complaint",
                "raise a grievance", "how to complain",
                "report an issue", "file a complaint")) {
            return "To submit a grievance:\n"
                    + "1. Open Submit Grievance.\n"
                    + "2. Describe the problem clearly.\n"
                    + "3. Provide the affected location.\n"
                    + "4. Complete the required fields.\n"
                    + "5. Attach evidence if requested.\n"
                    + "6. Review and submit the form.\n\n"
                    + "Follow the instructions displayed by the "
                    + "actual submission form.";
        }

        // 13. Complaint writing
        if (containsAny(text, "write a complaint",
                "complaint format", "how to describe",
                "what should i write", "help me write",
                "draft a complaint")) {
            return "A clear complaint should include:\n"
                    + "• The problem\n"
                    + "• The affected location\n"
                    + "• When it started\n"
                    + "• How it affects residents\n"
                    + "• Relevant evidence, if available\n\n"
                    + "Example: 'A large pothole near [location] "
                    + "is making travel unsafe. Please inspect "
                    + "and repair the damaged road.'";
        }

        // 14. Evidence and photographs
        if (containsAny(text, "upload photo", "upload image",
                "attach evidence", "add evidence",
                "photo of complaint", "upload proof")) {
            return "If the grievance form provides an evidence "
                    + "upload option, attach a clear, relevant "
                    + "photograph or document. Avoid including "
                    + "unnecessary personal information.";
        }

        // 15. Tracking guidance
        if (containsAny(text, "track", "complaint status",
                "grievance status", "my complaint",
                "check my complaint", "track my grievance")) {
            return "Open My Grievances or the tracking section "
                    + "of the website and check your complaint ID "
                    + "and displayed status. This chatbot does not "
                    + "yet retrieve live complaint records from "
                    + "your database.";
        }

        // 16. Complaint ID
        if (containsAny(text, "complaint id",
                "grievance id", "reference number",
                "acknowledgement number")) {
            return "After submitting a grievance, check the "
                    + "confirmation message for a reference ID. "
                    + "Keep it for future tracking.";
        }

        // 17. Category information
        if (containsAny(text, "list of categories",
                "grievance categories", "complaint categories",
                "what categories", "available categories")) {
            return "Configured categories include ROAD, WATER, "
                    + "ELECTRICITY, WASTE, DRAINAGE, STREETLIGHT, "
                    + "TRAFFIC, PUBLIC_TRANSPORT, PUBLIC_TOILETS, "
                    + "PARKS, ANIMAL_CONTROL, HEALTHCARE, EDUCATION, "
                    + "HOUSING, ENVIRONMENT, DISABILITY_ACCESS, "
                    + "PUBLIC_SAFETY and GOVERNMENT_SERVICES. "
                    + "Actual predictions depend on your classifier.";
        }

        // 18. Explain AI
        if (containsAny(text, "explain ai", "how does ai work",
                "how do you classify", "how is it classified",
                "how does classification work")) {
            return "SmartGrievance checks predefined issue rules "
                    + "first. If no rule matches, it uses the "
                    + "trained Tribuo machine-learning model.";
        }

        // 19. Privacy
        if (containsAny(text, "is my data safe",
                "privacy", "personal information",
                "is my information secure")) {
            return "Avoid sharing passwords, OTPs or sensitive "
                    + "personal details in this chat. Consult the "
                    + "platform administrator or privacy policy "
                    + "for specific security information.";
        }

        // 20. Emergency guidance
        if (containsAny(text, "emergency", "life threatening",
                "someone is in danger", "immediate danger",
                "someone is attacking me")) {
            return "If someone is in immediate danger in India, "
                    + "call 112 for emergency assistance. Move "
                    + "to a safe place if possible. Do not wait "
                    + "for this chatbot or ordinary grievance "
                    + "processing.";
        }

        // 21. Clarification for vague messages
        if (containsAny(text, "there is a problem",
                "problem in my area", "issue in my area",
                "something is wrong", "i have a problem",
                "civic issue in my locality")) {
            return "Could you describe the problem in more detail? "
                    + "Is it related to roads, drainage, garbage, "
                    + "water, electricity, traffic, safety or "
                    + "another community issue? Mention the "
                    + "location if possible.";
        }

        // 22. Detect multiple issue types
        List<String> detectedIssues = detectIssueTypes(text);

        if (detectedIssues.size() >= 2) {
            return "I noticed multiple problems in your message: "
                    + String.join(", ", detectedIssues) + ".\n\n"
                    + "Reporting each issue separately can help "
                    + "with assignment and tracking. Which issue "
                    + "would you like to report first?";
        }

        // 23. Parking guidance
        if (containsAny(text,
                "illegal parking", "wrong parking", "no parking",
                "parking problem", "parking issue",
                "parked on the footpath", "parked on footpath",
                "someone parked on the footpath",
                "someone parked on footpath",
                "bike parked on footpath",
                "bike parked on the footpath",
                "motorcycle parked on footpath",
                "motorcycle parked on the footpath",
                "car blocking my gate",
                "car blocking the road",
                "car blocking the footpath",
                "vehicle blocking the footpath",
                "vehicle parked on footpath",
                "vehicle parked on the footpath",
                "parking on footpath",
                "parking on the footpath",
                "footpath blocked by vehicles")) {

            return "This sounds like a parking or pedestrian-access "
                    + "problem. In Bengaluru, check whether the "
                    + "Bengaluru Traffic Police Public Eye app "
                    + "supports reporting this violation. For other "
                    + "locations, contact the local traffic police "
                    + "or relevant civic authority. Record evidence "
                    + "only if safe.";
        }

        // 24. Road rage and aggressive driving
        if (containsAny(text, "road rage",
                "driver threatening me",
                "driver is threatening",
                "aggressive driver",
                "someone attacked me on the road",
                "driver is following me",
                "dangerous driving",
                "threatened by another driver")) {

            return "Road rage can become dangerous. Move to a "
                    + "safe place and avoid confrontation. If "
                    + "violence, threats or immediate danger are "
                    + "present in India, call 112. For non-emergency "
                    + "incidents, contact local police or traffic "
                    + "police. Record details only if safe.";
        }

        // 25. Classify one civic issue and assess priority
        if (isCivicIssue(text)) {
            try {
                String category =
                        categoryService.predictCategory(message);

                String reason =
                        categoryService.explainCategory(message);

                String priority = assessPriority(message);

                return "Predicted grievance category: "
                        + category + "\n"
                        + "Suggested priority: " + priority + "\n\n"
                        + "Explanation: " + reason + "\n\n"
                        + "This is an initial automated assessment. "
                        + "Please verify the category and priority "
                        + "before submitting your grievance.";
            } catch (Exception e) {
                return "I'm unable to classify this issue right "
                        + "now. Please try again or use the regular "
                        + "grievance submission form.";
            }
        }

        // 26. Generic fallback
        return "I'd like to help! I can assist with SmartGrievance, "
                + "account guidance, complaint writing, common "
                + "residential problems and civic issue classification. "
                + "Could you describe your question or problem "
                + "in a little more detail?";
    }

    // Priority assessment: preliminary keyword-based rules.
    private String assessPriority(String message) {

        String text = message.toLowerCase(Locale.ROOT);

        // URGENT: immediate danger and serious hazards
        if (containsAny(text,
                "exposed live wire", "live electrical wire",
                "electric shock", "someone is in danger",
                "immediate danger", "life threatening",
                "someone is attacking me",
                "driver threatening me",
                "someone attacked me on the road",
                "missing manhole cover", "open manhole",
                "fallen tree blocking road")) {
            return "URGENT";
        }

        // HIGH: potentially serious problems
        if (containsAny(text,
                "sewage overflow", "sewage leak",
                "major water leakage", "flooding",
                "dangerous road condition", "large pothole",
                "road collapse", "structural damage",
                "aggressive dog", "dog bite")) {
            return "HIGH";
        }

        // MEDIUM: common maintenance issues
        if (containsAny(text,
                "pothole", "blocked drain", "clogged drain",
                "stagnant water", "garbage not collected",
                "garbage collection", "streetlight",
                "street light", "water leakage",
                "water shortage", "illegal parking",
                "parked on the footpath", "parked on footpath",
                "broken footpath", "noise pollution")) {
            return "MEDIUM";
        }

        // LOW is only a provisional default.
        // Unrecognized hazards should be reviewed.
        return "LOW";
    }

    // Detect multiple different issue types.
    private List<String> detectIssueTypes(String text) {

        List<String> issues = new ArrayList<>();

        if (containsAny(text, "blocked drain", "drainage",
                "stagnant water", "sewage overflow",
                "clogged drain", "open drain",
                "mosquito breeding")) {
            issues.add("Drainage");
        }

        if (containsAny(text, "garbage", "waste dumping",
                "overflowing bin", "uncollected waste",
                "trash", "litter", "illegal dumping")) {
            issues.add("Waste management");
        }

        if (containsAny(text, "pothole", "damaged road",
                "broken road", "broken footpath",
                "damaged footpath", "broken pavement")) {
            issues.add("Road or footpath");
        }

        if (containsAny(text, "streetlight", "street light",
                "street lamp", "lamp post", "dark street")) {
            issues.add("Street lighting");
        }

        if (containsAny(text, "water shortage",
                "water leakage", "water supply",
                "water pipe", "dirty water",
                "low water pressure")) {
            issues.add("Water supply");
        }

        if (containsAny(text, "power outage",
                "electricity problem", "exposed electrical wire",
                "electrical fault", "electric shock")) {
            issues.add("Electricity");
        }

        if (containsAny(text, "illegal parking",
                "wrong parking", "parking issue",
                "traffic jam", "traffic congestion",
                "road rage", "parked on the footpath",
                "parked on footpath",
                "vehicle parked on the footpath",
                "bike parked on footpath")) {
            issues.add("Traffic or parking");
        }

        if (containsAny(text, "public toilet",
                "broken park bench", "damaged playground",
                "broken bus shelter")) {
            issues.add("Public amenities");
        }

        if (containsAny(text, "noise pollution",
                "loud noise", "construction noise",
                "air pollution", "dust pollution")) {
            issues.add("Noise or pollution");
        }

        if (containsAny(text, "stray dog", "stray cattle",
                "injured animal", "aggressive dog")) {
            issues.add("Animal-related issue");
        }

        return issues;
    }

    private boolean matches(String text, String... phrases) {
        for (String phrase : phrases) {
            if (text.equals(phrase)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String text, String... phrases) {
        for (String phrase : phrases) {
            if (text.contains(phrase)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCivicIssue(String text) {

        String[] indicators = {
                // Roads and pedestrian access
                "pothole", "damaged road", "broken road",
                "road crack", "road damage", "broken footpath",
                "damaged footpath", "broken pavement",
                "open manhole", "missing manhole cover",
                "parked on the footpath", "parked on footpath",
                "vehicle parked on footpath",
                "bike parked on footpath",
                "footpath blocked by vehicles",

                // Water and drainage
                "water supply", "water leakage",
                "water shortage", "drinking water",
                "water pipe", "dirty water", "blocked drain",
                "drainage", "sewage overflow", "stagnant water",
                "mosquito breeding", "clogged drain", "open drain",

                // Waste
                "garbage", "waste dumping", "trash", "litter",
                "overflowing garbage bin",
                "missed garbage collection", "illegal dumping",

                // Electricity and streetlights
                "streetlight", "street light", "lamp post",
                "power outage", "electricity problem",
                "electrical wire", "electric shock",
                "dark street", "exposed electrical wire",

                // Traffic and parking
                "traffic congestion", "traffic jam",
                "illegal parking", "wrong parking",
                "parking issue", "parking problem",
                "road rage", "dangerous driving",
                "car blocking my gate", "car blocking the road",
                "car blocking the footpath",

                // Public amenities
                "public toilet", "public park",
                "broken park bench", "damaged playground",
                "bus shelter",

                // Animals
                "stray dog", "stray cattle", "stray animal",
                "injured animal", "aggressive dog",

                // Public services
                "government hospital", "health centre",
                "government school", "government office",
                "municipal service", "civic issue",
                "unresolved civic complaint",

                // Accessibility and housing
                "wheelchair ramp", "accessibility problem",
                "housing problem", "structural damage",
                "no wheelchair access",

                // Environment and noise
                "air pollution", "noise pollution",
                "water pollution", "noise disturbance",
                "construction noise", "construction dust",
                "burning waste", "fallen tree", "fallen branch"
        };

        return containsAny(text, indicators);
    }
}

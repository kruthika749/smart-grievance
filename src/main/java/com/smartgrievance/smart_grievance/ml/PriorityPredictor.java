package com.smartgrievance.smart_grievance.ml;

public class PriorityPredictor {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" Smart Grievance AI Priority Testing");
        System.out.println("========================================");

        String[] grievances = {

            "There is a minor issue with the park benches that needs routine maintenance.",

            "Garbage collection has been delayed for two days in our street.",

            "There has been no drinking water supply in our area for several days.",

            "A serious electrical fault is causing repeated power failures in our neighborhood.",

            "A dangerous public safety situation is putting residents at immediate risk.",

            "The government hospital is overcrowded and patients urgently need medical attention.",

            "The school classroom needs some routine maintenance work.",

            "Several families are living in an unsafe residential building with structural damage."
        };

        String[] expectedPriorities = {

            "LOW",
            "MEDIUM",
            "HIGH",
            "HIGH",
            "CRITICAL",
            "HIGH",
            "LOW",
            "CRITICAL"
        };

        int correct = 0;

        for (int i = 0; i < grievances.length; i++) {

            String grievance = grievances[i];

            String predictedPriority =
                    predictPriority(grievance);

            boolean isCorrect =
                    predictedPriority.equals(
                            expectedPriorities[i]);

            if (isCorrect) {
                correct++;
            }

            System.out.println();
            System.out.println(
                    "Test " + (i + 1));

            System.out.println(
                    "Grievance: " + grievance);

            System.out.println(
                    "Expected Priority: "
                            + expectedPriorities[i]);

            System.out.println(
                    "AI Priority: "
                            + predictedPriority);

            System.out.println(
                    "Reason: "
                            + explainPriority(grievance));

            System.out.println(
                    "Result: "
                            + (isCorrect
                            ? "CORRECT"
                            : "INCORRECT"));
        }

        double accuracy =
                ((double) correct
                        / grievances.length)
                        * 100.0;

        System.out.println();
        System.out.println("========================================");
        System.out.println(" PRIORITY TEST RESULTS");
        System.out.println("========================================");

        System.out.println(
                "Total tests: "
                        + grievances.length);

        System.out.println(
                "Correct predictions: "
                        + correct);

        System.out.println(
                "Incorrect predictions: "
                        + (grievances.length - correct));

        System.out.printf(
                "Test accuracy: %.2f%%%n",
                accuracy);

        System.out.println("========================================");
        System.out.println(
                " AI PRIORITY TESTING COMPLETED");
        System.out.println("========================================");
    }


    public static String predictPriority(
            String grievance) {

        if (grievance == null ||
                grievance.trim().isEmpty()) {

            return "MEDIUM";
        }

        String text =
                grievance.toLowerCase();


        // --------------------------------
        // CRITICAL PRIORITY
        // --------------------------------

        if (containsAny(
                text,
                "life threatening",
                "life-threatening",
                "immediate danger",
                "immediate risk",
                "emergency",
                "severe injury",
                "injury risk",
                "shock risk",
                "fire risk",
                "structural collapse",
                "collapse risk",
                "danger to residents",
                "people at immediate risk",
                "unsafe building",
                "structural damage",
                "building unsafe",
                "building collapse",
                "risk of collapse")) {

            return "CRITICAL";
        }


        // --------------------------------
        // HIGH PRIORITY
        // --------------------------------

        if (containsAny(
                text,
                "urgent",
                "urgently",
                "serious",
                "severe",
                "major disruption",
                "essential service unavailable",
                "several days",
                "significant risk",
                "needs immediate attention",
                "power failure",
                "no drinking water")) {

            return "HIGH";
        }


        // --------------------------------
        // MEDIUM PRIORITY
        // --------------------------------

        if (containsAny(
                text,
                "moderate",
                "recurring",
                "ongoing",
                "affecting daily",
                "affecting residents",
                "needs attention",
                "needs repair",
                "inconvenience",
                "partially disrupted")) {

            return "MEDIUM";
        }


        // --------------------------------
        // LOW PRIORITY
        // --------------------------------

        if (containsAny(
                text,
                "minor",
                "small issue",
                "routine",
                "cosmetic",
                "slight",
                "routine maintenance",
                "non urgent",
                "no immediate danger")) {

            return "LOW";
        }


        // Default priority
        return "MEDIUM";
    }


    // ========================================
    // EXPLAINABLE AI - PRIORITY REASON
    // ========================================

    public static String explainPriority(
            String grievance) {

        if (grievance == null ||
                grievance.trim().isEmpty()) {

            return "No grievance text was provided, so the system used the default MEDIUM priority.";
        }

        String text =
                grievance.toLowerCase();


        // --------------------------------
        // CRITICAL REASON
        // --------------------------------

        if (containsAny(
                text,
                "life threatening",
                "life-threatening",
                "immediate danger",
                "immediate risk",
                "emergency",
                "severe injury",
                "injury risk",
                "shock risk",
                "fire risk",
                "structural collapse",
                "collapse risk",
                "danger to residents",
                "people at immediate risk",
                "unsafe building",
                "structural damage",
                "building unsafe",
                "building collapse",
                "risk of collapse")) {

            return "The grievance contains an immediate safety, danger, injury, fire, or structural-risk indicator.";
        }


        // --------------------------------
        // HIGH REASON
        // --------------------------------

        if (containsAny(
                text,
                "urgent",
                "urgently",
                "serious",
                "severe",
                "major disruption",
                "essential service unavailable",
                "several days",
                "significant risk",
                "needs immediate attention",
                "power failure",
                "no drinking water")) {

            return "The grievance indicates urgency, prolonged disruption, significant risk, or failure of an essential service.";
        }


        // --------------------------------
        // MEDIUM REASON
        // --------------------------------

        if (containsAny(
                text,
                "moderate",
                "recurring",
                "ongoing",
                "affecting daily",
                "affecting residents",
                "needs attention",
                "needs repair",
                "inconvenience",
                "partially disrupted")) {

            return "The grievance indicates an ongoing, recurring, repair-related, or moderate civic issue.";
        }


        // --------------------------------
        // LOW REASON
        // --------------------------------

        if (containsAny(
                text,
                "minor",
                "small issue",
                "routine",
                "cosmetic",
                "slight",
                "routine maintenance",
                "non urgent",
                "no immediate danger")) {

            return "The grievance describes a minor, routine, cosmetic, or non-urgent issue.";
        }


        // --------------------------------
        // DEFAULT REASON
        // --------------------------------

        return "No specific severity indicator was detected, so the system used the default MEDIUM priority.";
    }


    // ========================================
    // KEYWORD CHECK
    // ========================================

    private static boolean containsAny(
            String text,
            String... keywords) {

        for (String keyword : keywords) {

            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}
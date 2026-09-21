package com.smartgrievance.smart_grievance;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class AIResolutionVerificationService {

    public VerificationResult verifyResolution(
            String grievance,
            String afterEvidence) {

        if (grievance == null ||
                grievance.trim().isEmpty()) {

            return new VerificationResult(
                    false,
                    0,
                    "Original grievance information is missing."
            );
        }

        if (afterEvidence == null ||
                afterEvidence.trim().isEmpty()) {

            return new VerificationResult(
                    false,
                    0,
                    "Resolution evidence is missing."
            );
        }

        Set<String> grievanceWords =
                extractKeywords(grievance);

        Set<String> evidenceWords =
                extractKeywords(afterEvidence);

        int matchedWords = 0;

        for (String word : grievanceWords) {

            if (evidenceWords.contains(word)) {
                matchedWords++;
            }
        }

        int score = 0;

        if (!grievanceWords.isEmpty()) {

            score = (matchedWords * 100)
                    / grievanceWords.size();
        }

        boolean relevant = score >= 20;

        String message;

        if (relevant) {

            message =
                    "Resolution evidence appears relevant "
                    + "to the reported grievance.";
        } else {

            message =
                    "Resolution evidence does not contain "
                    + "enough information related to the "
                    + "reported grievance.";
        }

        return new VerificationResult(
                relevant,
                score,
                message
        );
    }

    private Set<String> extractKeywords(String text) {

        Set<String> words = new HashSet<>();

        String cleanedText =
                text.toLowerCase()
                        .replaceAll("[^a-zA-Z0-9 ]", " ");

        String[] tokens =
                cleanedText.split("\\s+");

        for (String token : tokens) {

            if (token.length() >= 4 &&
                    !isStopWord(token)) {

                words.add(token);
            }
        }

        return words;
    }

    private boolean isStopWord(String word) {

        return word.equals("this")
                || word.equals("that")
                || word.equals("with")
                || word.equals("from")
                || word.equals("have")
                || word.equals("been")
                || word.equals("were")
                || word.equals("they")
                || word.equals("their")
                || word.equals("there")
                || word.equals("near")
                || word.equals("into")
                || word.equals("after")
                || word.equals("before")
                || word.equals("will")
                || word.equals("your");
    }

    public static class VerificationResult {

        private boolean relevant;
        private int score;
        private String message;

        public VerificationResult(
                boolean relevant,
                int score,
                String message) {

            this.relevant = relevant;
            this.score = score;
            this.message = message;
        }

        public boolean isRelevant() {
            return relevant;
        }

        public int getScore() {
            return score;
        }

        public String getMessage() {
            return message;
        }
    }
}
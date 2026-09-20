package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DuplicateGrievanceService {

    private final GrievanceRepository grievanceRepository;

    public DuplicateGrievanceService(
            GrievanceRepository grievanceRepository) {

        this.grievanceRepository = grievanceRepository;
    }

    public Grievance findSimilarGrievance(
            String grievanceText,
            String location,
            String category) {

        List<Grievance> existingGrievances =
                grievanceRepository.findAll();

        String newText = normalize(grievanceText);
        String newLocation = normalize(location);
        String newCategory = normalize(category);

        for (Grievance existing : existingGrievances) {

            if (existing.getGrievance() == null) {
                continue;
            }

            String existingText =
                    normalize(existing.getGrievance());

            String existingLocation =
                    normalize(existing.getLocation());

            String existingCategory =
                    normalize(existing.getCategory());

            /*
             * Ignore old grievances that do not have
             * an AI category.
             */
            if (existingCategory.isEmpty()) {
                continue;
            }

            /*
             * Category must match.
             */
            boolean sameCategory =
                    !newCategory.isEmpty()
                    && newCategory.equalsIgnoreCase(
                            existingCategory
                    );

            if (!sameCategory) {
                continue;
            }

            /*
             * Location comparison.
             */
            boolean sameLocation =
                    !newLocation.isEmpty()
                    && !existingLocation.isEmpty()
                    && newLocation.equalsIgnoreCase(
                            existingLocation
                    );

            /*
             * Calculate text similarity.
             */
            double similarity =
                    calculateSimilarity(
                            newText,
                            existingText
                    );

            /*
             * Duplicate rules:
             *
             * 1. Same category + same location
             *    + similarity >= 0.35
             *
             * OR
             *
             * 2. Same category
             *    + similarity >= 0.70
             */
            if ((sameLocation && similarity >= 0.35)
                    || similarity >= 0.70) {

                return existing;
            }
        }

        return null;
    }

    private double calculateSimilarity(
            String text1,
            String text2) {

        if (text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        String[] words1 = text1.split(" ");
        String[] words2 = text2.split(" ");

        int matchingWords = 0;

        for (String word1 : words1) {

            if (word1.length() < 3) {
                continue;
            }

            for (String word2 : words2) {

                if (word1.equals(word2)) {
                    matchingWords++;
                    break;
                }
            }
        }

        int totalWords =
                Math.max(
                        words1.length,
                        words2.length
                );

        return (double) matchingWords
                / totalWords;
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
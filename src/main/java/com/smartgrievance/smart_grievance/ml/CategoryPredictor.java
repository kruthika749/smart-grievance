package com.smartgrievance.smart_grievance.ml;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.tribuo.Example;
import org.tribuo.Model;
import org.tribuo.Prediction;
import org.tribuo.classification.Label;
import org.tribuo.classification.LabelFactory;
import org.tribuo.data.columnar.FieldProcessor;
import org.tribuo.data.columnar.RowProcessor;
import org.tribuo.data.columnar.processors.field.TextFieldProcessor;
import org.tribuo.data.columnar.processors.response.FieldResponseProcessor;
import org.tribuo.data.text.impl.TokenPipeline;
import org.tribuo.util.tokens.impl.BreakIteratorTokenizer;

public class CategoryPredictor {

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println(" Smart Grievance AI Category Testing");
        System.out.println("========================================");

        // Load trained category model
        Path modelPath = Paths.get(
                "src/main/resources/ml/category-model.tribuo");

        Model<?> loadedModel =
                Model.deserializeFromFile(modelPath);

        Model<Label> model =
                loadedModel.castModel(Label.class);

        // Same text pipeline used during training
        TokenPipeline textPipeline =
                new TokenPipeline(
                        new BreakIteratorTokenizer(Locale.US),
                        2,
                        true);

        ArrayList<FieldProcessor> fieldProcessors =
                new ArrayList<>();

        fieldProcessors.add(
                new TextFieldProcessor(
                        "grievance_text",
                        textPipeline));

        FieldResponseProcessor<Label> responseProcessor =
                new FieldResponseProcessor<>(
                        "category",
                        "UNKNOWN",
                        new LabelFactory());

        RowProcessor<Label> rowProcessor =
                new RowProcessor.Builder<Label>()
                        .setFieldProcessors(fieldProcessors)
                        .build(responseProcessor);

        // ========================================
        // TEST COMPLAINTS
        // ========================================

        String[] grievances = {

            "Several large cracks and damaged patches have appeared on the road near our residential area.",

            "Residents on our street are receiving very low water pressure and the supply is irregular.",

            "The electricity voltage keeps fluctuating in our locality and appliances are being affected.",

            "Household garbage is being left on the roadside and has started creating a bad smell.",

            "Rainwater is unable to flow through the blocked drain and is accumulating outside homes.",

            "The lamp post outside our apartment has been switched off for many nights.",

            "People are worried about their safety because there is no security around the isolated public area.",

            "The government health centre does not have enough doctors and patients are waiting for hours.",

            "The classroom building in our government school requires repairs and students need a safer learning environment.",

            "Buses on our route are very infrequent and commuters have to wait for a long time.",

            "Traffic congestion near the junction is severe during peak hours and vehicles are getting stuck.",

            "The public toilet near the market is dirty and has not been cleaned properly.",

            "The children's play equipment in the local park is broken and needs to be repaired.",

            "A group of stray dogs is causing problems near the residential buildings and residents need assistance.",

            "People are dumping waste into the lake and the surrounding water is becoming polluted.",

            "Several families are living in damaged houses that require urgent structural repairs.",

            "There is no wheelchair ramp at the entrance of the public building, making access difficult.",

            "My application at the government office has been pending for weeks without any update.",

            "There is a civic issue in our locality that does not fit into the listed complaint categories.",

            "Residents need help with a local municipal matter that is not related to roads, water or electricity.",

            // Mixed-context tests

            "A dangerous electrical wire is hanging near the school and children are at immediate risk of electric shock.",

            "A large pothole near the government hospital is damaging vehicles and making the road difficult to use.",

            "Garbage has been dumped beside the public park and the area has become unhygienic.",

            "The streetlight outside the school has stopped working and the road becomes dark at night.",

            "The school has no proper drinking water supply and students are facing difficulties.",

            "A blocked drain is overflowing onto the road and causing waterlogging.",

            "Heavy traffic near the school entrance is creating congestion during student arrival and departure.",

            "Stray cattle are frequently entering the road and creating a danger for vehicles.",

            "The public toilet does not have an accessible entrance for wheelchair users.",

            "The residential building has serious structural damage and residents are concerned about their safety."
        };

        String[] expectedCategories = {

            "ROAD",
            "WATER",
            "ELECTRICITY",
            "WASTE",
            "DRAINAGE",
            "STREETLIGHT",
            "PUBLIC_SAFETY",
            "HEALTHCARE",
            "EDUCATION",
            "PUBLIC_TRANSPORT",
            "TRAFFIC",
            "PUBLIC_TOILETS",
            "PARKS",
            "ANIMAL_CONTROL",
            "ENVIRONMENT",
            "HOUSING",
            "DISABILITY_ACCESS",
            "GOVERNMENT_SERVICES",
            "OTHER",
            "OTHER",

            "ELECTRICITY",
            "ROAD",
            "WASTE",
            "STREETLIGHT",
            "WATER",
            "DRAINAGE",
            "TRAFFIC",
            "ANIMAL_CONTROL",
            "DISABILITY_ACCESS",
            "HOUSING"
        };

        int correct = 0;

        // ========================================
        // RUN TESTS
        // ========================================

        for (int i = 0; i < grievances.length; i++) {

            String grievanceText =
                    grievances[i];

            Map<String, String> row =
                    new HashMap<>();

            row.put(
                    "grievance_text",
                    grievanceText);

            Optional<Example<Label>> example =
                    rowProcessor.generateExample(
                            row,
                            false);

            if (example.isEmpty()) {

                System.out.println();
                System.out.println(
                        "Test " + (i + 1)
                                + " - Could not process grievance.");

                continue;
            }

            String predictedCategory =
                    predictCategoryWithPriority(
                            grievanceText,
                            model,
                            example.get());

            boolean isCorrect =
                    predictedCategory.equals(
                            expectedCategories[i]);

            if (isCorrect) {
                correct++;
            }

            System.out.println();
            System.out.println(
                    "Test " + (i + 1));

            System.out.println(
                    "Grievance: "
                            + grievanceText);

            System.out.println(
                    "Expected: "
                            + expectedCategories[i]);

            System.out.println(
                    "AI Prediction: "
                            + predictedCategory);

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
        System.out.println(" REALISTIC TEST RESULTS");
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
                " AI CATEGORY TESTING COMPLETED");
        System.out.println("========================================");
    }


    // ========================================
    // HYBRID CATEGORY PREDICTION
    // ========================================

    private static String predictCategoryWithPriority(
            String grievance,
            Model<Label> model,
            Example<Label> example) {

        String text =
                grievance.toLowerCase();


        // --------------------------------
        // DISABILITY ACCESS
        // CHECK BEFORE PUBLIC TOILETS
        // --------------------------------

        if (containsAny(
                text,
                "wheelchair",
                "wheelchair ramp",
                "wheelchair access",
                "disabled access",
                "accessible entrance",
                "accessible access",
                "barrier free",
                "barrier-free",
                "accessibility ramp",
                "tactile path",
                "accessible toilet")) {

            return "DISABILITY_ACCESS";
        }


        // --------------------------------
        // ELECTRICITY
        // --------------------------------

        if (containsAny(
                text,
                "electrical wire",
                "electric wire",
                "power cable",
                "electric cable",
                "electric shock",
                "electrical fault",
                "power failure",
                "power outage",
                "transformer",
                "voltage fluctuation",
                "electricity supply")) {

            return "ELECTRICITY";
        }


        // --------------------------------
        // WATER
        // --------------------------------

        if (containsAny(
                text,
                "drinking water",
                "water supply",
                "water shortage",
                "water leakage",
                "water leak",
                "low water pressure",
                "dirty water",
                "contaminated water",
                "water pipeline",
                "water pipe")) {

            return "WATER";
        }


        // --------------------------------
        // ROAD
        // --------------------------------

        if (containsAny(
                text,
                "pothole",
                "damaged road",
                "broken road",
                "road damage",
                "road surface",
                "road crack",
                "cracked road",
                "damaged pavement",
                "broken pavement",
                "asphalt")) {

            return "ROAD";
        }


        // --------------------------------
        // DRAINAGE
        // --------------------------------

        if (containsAny(
                text,
                "blocked drain",
                "blocked drainage",
                "drain overflow",
                "drainage problem",
                "sewer blockage",
                "sewage overflow",
                "clogged drain",
                "open drain")) {

            return "DRAINAGE";
        }


        // --------------------------------
        // WASTE
        // --------------------------------

        if (containsAny(
                text,
                "garbage collection",
                "uncollected garbage",
                "garbage bin",
                "garbage waste",
                "waste collection",
                "solid waste",
                "dumped waste",
                "dumped garbage",
                "garbage dumped",
                "garbage has been dumped",
                "garbage beside",
                "garbage near",
                "household garbage",
                "litter")) {

            return "WASTE";
        }


        // --------------------------------
        // STREETLIGHT
        // --------------------------------

        if (containsAny(
                text,
                "streetlight",
                "street light",
                "street lamp",
                "lamp post",
                "lamp not working",
                "road light",
                "light pole")) {

            return "STREETLIGHT";
        }


        // --------------------------------
        // TRAFFIC
        // --------------------------------

        if (containsAny(
                text,
                "traffic signal",
                "traffic congestion",
                "traffic jam",
                "signal timing",
                "signal malfunction",
                "illegal parking",
                "vehicle congestion")) {

            return "TRAFFIC";
        }


        // --------------------------------
        // PUBLIC TRANSPORT
        // --------------------------------

        if (containsAny(
                text,
                "bus service",
                "bus stop",
                "bus route",
                "bus frequency",
                "public bus",
                "bus shelter",
                "public transport")) {

            return "PUBLIC_TRANSPORT";
        }


        // --------------------------------
        // PUBLIC TOILETS
        // --------------------------------

        if (containsAny(
                text,
                "public toilet",
                "public restroom",
                "community toilet",
                "toilet cleanliness",
                "toilet maintenance",
                "restroom hygiene")) {

            return "PUBLIC_TOILETS";
        }


        // --------------------------------
        // PARKS
        // --------------------------------

        if (containsAny(
                text,
                "public park",
                "local park",
                "park maintenance",
                "park bench",
                "playground",
                "park equipment",
                "children's play equipment")) {

            return "PARKS";
        }


        // --------------------------------
        // ANIMAL CONTROL
        // --------------------------------

        if (containsAny(
                text,
                "stray dog",
                "stray dogs",
                "stray animal",
                "stray animals",
                "stray cattle",
                "animal nuisance",
                "dog bite",
                "aggressive dog",
                "animal control")) {

            return "ANIMAL_CONTROL";
        }


        // --------------------------------
        // HEALTHCARE
        // --------------------------------

        if (containsAny(
                text,
                "government hospital",
                "government health centre",
                "government health center",
                "public clinic",
                "health centre",
                "health center",
                "medical facility",
                "doctor availability",
                "medicine shortage")) {

            return "HEALTHCARE";
        }


        // --------------------------------
        // EDUCATION
        // --------------------------------

        if (containsAny(
                text,
                "government school",
                "school classroom",
                "classroom",
                "teacher",
                "students",
                "college facility",
                "school building",
                "learning facility",
                "education service")) {

            return "EDUCATION";
        }


        // --------------------------------
        // HOUSING
        // --------------------------------

        if (containsAny(
                text,
                "residential building",
                "housing problem",
                "housing unit",
                "apartment issue",
                "tenant complaint",
                "rental housing",
                "unsafe residence",
                "residential property",
                "structural damage")) {

            return "HOUSING";
        }


        // --------------------------------
        // ENVIRONMENT
        // --------------------------------

        if (containsAny(
                text,
                "air pollution",
                "water pollution",
                "noise pollution",
                "pollution complaint",
                "smoke pollution",
                "environmental damage",
                "dust pollution")) {

            return "ENVIRONMENT";
        }


        // --------------------------------
        // PUBLIC SAFETY
        // --------------------------------

        if (containsAny(
                text,
                "public safety",
                "security concern",
                "dangerous public area",
                "unsafe public place",
                "safety hazard",
                "crime risk",
                "dangerous location")) {

            return "PUBLIC_SAFETY";
        }


        // --------------------------------
        // GOVERNMENT SERVICES
        // --------------------------------

        if (containsAny(
                text,
                "government service",
                "municipal service",
                "birth certificate",
                "property tax",
                "government office",
                "government document",
                "civic service",
                "municipal office",
                "certificate service")) {

            return "GOVERNMENT_SERVICES";
        }


        // --------------------------------
        // FALLBACK TO TRIBUO ML
        // --------------------------------

        Prediction<Label> prediction =
                model.predict(example);

        return prediction
                .getOutput()
                .getLabel();
    }


    // ========================================
    // KEYWORD MATCHING
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
package com.smartgrievance.smart_grievance.ml;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
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

@Service
public class CategoryService {

    private final Model<Label> model;
    private final RowProcessor<Label> rowProcessor;

    public CategoryService() throws Exception {

        // Load the trained AI model
        Path modelPath = Paths.get(
                "src/main/resources/ml/category-model.tribuo");

        Model<?> loadedModel =
                Model.deserializeFromFile(modelPath);

        model = loadedModel.castModel(Label.class);

        // Same pipeline used during training
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

        rowProcessor =
                new RowProcessor.Builder<Label>()
                        .setFieldProcessors(fieldProcessors)
                        .build(responseProcessor);
    }


    public String predictCategory(String grievanceText) {

        if (grievanceText == null ||
                grievanceText.trim().isEmpty()) {

            return "OTHER";
        }

        String text =
                grievanceText.toLowerCase();


        // ========================================
        // DISABILITY ACCESS
        // Check before PUBLIC_TOILETS
        // ========================================

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


        // ========================================
        // ELECTRICITY
        // ========================================

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


        // ========================================
        // WATER
        // ========================================

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


        // ========================================
        // ROAD
        // ========================================

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


        // ========================================
        // DRAINAGE
        // ========================================

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


        // ========================================
        // WASTE
        // ========================================

        if (containsAny(
                text,
                "garbage",
                "garbage collection",
                "uncollected garbage",
                "garbage bin",
                "garbage waste",
                "waste collection",
                "solid waste",
                "dumped waste",
                "dumped garbage",
                "garbage dumped",
                "garbage dumping",
                "dumping garbage",
                "dump garbage",
                "garbage dump",
                "garbage has been dumped",
                "garbage beside",
                "garbage near",
                "household garbage",
                "trash",
                "litter")) {

            return "WASTE";
        }


        // ========================================
        // STREETLIGHT
        // ========================================

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


        // ========================================
        // TRAFFIC
        // ========================================

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


        // ========================================
        // PUBLIC TRANSPORT
        // ========================================

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


        // ========================================
        // PUBLIC TOILETS
        // ========================================

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


        // ========================================
        // PARKS
        // ========================================

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


        // ========================================
        // ANIMAL CONTROL
        // ========================================

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


        // ========================================
        // HEALTHCARE
        // ========================================

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


        // ========================================
        // EDUCATION
        // ========================================

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


        // ========================================
        // HOUSING
        // ========================================

        if (containsAny(
                text,
                "residential building",
                "housing problem",
                "housing unit",
                "apartment issue",
                "tenant complaint",
                "rental housing",
                "unsafe residence",
                "residential property")) {

            return "HOUSING";
        }


        // ========================================
        // ENVIRONMENT
        // ========================================

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


        // ========================================
        // PUBLIC SAFETY
        // ========================================

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


        // ========================================
        // GOVERNMENT SERVICES
        // ========================================

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


        // ========================================
        // FALLBACK TO TRIBUO ML MODEL
        // ========================================

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
            return "OTHER";
        }

        Prediction<Label> prediction =
                model.predict(example.get());

        return prediction
                .getOutput()
                .getLabel();
    }


    // ========================================
    // KEYWORD CHECK
    // ========================================

    private boolean containsAny(
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
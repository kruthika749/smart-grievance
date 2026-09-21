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


    // ========================================
    // CATEGORY PREDICTION
    // ========================================

    public String predictCategory(String grievanceText) {

        if (grievanceText == null ||
                grievanceText.trim().isEmpty()) {

            return "OTHER";
        }

        String text =
                grievanceText.toLowerCase();


        // ========================================
        // DISABILITY ACCESS
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
    // EXPLAINABLE AI - CATEGORY REASON
    // ========================================

    public String explainCategory(
            String grievanceText) {

        if (grievanceText == null ||
                grievanceText.trim().isEmpty()) {

            return "No grievance text was provided, so the system classified the complaint as OTHER.";
        }

        String text =
                grievanceText.toLowerCase();


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

            return "Accessibility-related terms were detected, indicating a disability access issue.";
        }


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

            return "Electricity-related terms such as electrical wire, power failure, or electricity supply were detected.";
        }


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

            return "Water-related terms such as water supply, leakage, shortage, or water pipeline were detected.";
        }


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

            return "Road-related terms such as pothole, damaged road, pavement, or road cracks were detected.";
        }


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

            return "Drainage-related terms such as blocked drain, sewage overflow, or drainage problems were detected.";
        }


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

            return "Garbage and waste-management terms were detected, indicating a solid-waste issue.";
        }


        if (containsAny(
                text,
                "streetlight",
                "street light",
                "street lamp",
                "lamp post",
                "lamp not working",
                "road light",
                "light pole")) {

            return "Streetlight-related terms such as street lamp, lamp post, or non-working light were detected.";
        }


        if (containsAny(
                text,
                "traffic signal",
                "traffic congestion",
                "traffic jam",
                "signal timing",
                "signal malfunction",
                "illegal parking",
                "vehicle congestion")) {

            return "Traffic-related terms such as congestion, traffic signals, or illegal parking were detected.";
        }


        if (containsAny(
                text,
                "bus service",
                "bus stop",
                "bus route",
                "bus frequency",
                "public bus",
                "bus shelter",
                "public transport")) {

            return "Public-transport terms such as bus service, bus stop, route, or frequency were detected.";
        }


        if (containsAny(
                text,
                "public toilet",
                "public restroom",
                "community toilet",
                "toilet cleanliness",
                "toilet maintenance",
                "restroom hygiene")) {

            return "Public-toilet and sanitation terms were detected.";
        }


        if (containsAny(
                text,
                "public park",
                "local park",
                "park maintenance",
                "park bench",
                "playground",
                "park equipment",
                "children's play equipment")) {

            return "Park and recreational-facility terms were detected.";
        }


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

            return "Animal-related terms such as stray animals, dog bites, or animal nuisance were detected.";
        }


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

            return "Healthcare-related terms such as hospital, clinic, medical facility, or medicine shortage were detected.";
        }


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

            return "Education-related terms such as school, classroom, teacher, or students were detected.";
        }


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

            return "Housing and residential-property terms were detected.";
        }


        if (containsAny(
                text,
                "air pollution",
                "water pollution",
                "noise pollution",
                "pollution complaint",
                "smoke pollution",
                "environmental damage",
                "dust pollution")) {

            return "Environmental and pollution-related terms were detected.";
        }


        if (containsAny(
                text,
                "public safety",
                "security concern",
                "dangerous public area",
                "unsafe public place",
                "safety hazard",
                "crime risk",
                "dangerous location")) {

            return "Public-safety and security-related terms were detected.";
        }


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

            return "Government and municipal service-related terms were detected.";
        }


        return "No predefined category keywords were detected, so the trained ML model was used to classify the grievance.";
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
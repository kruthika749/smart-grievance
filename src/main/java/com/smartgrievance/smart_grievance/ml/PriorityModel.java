package com.smartgrievance.smart_grievance.ml;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.classification.Label;
import org.tribuo.classification.LabelFactory;
import org.tribuo.classification.sgd.linear.LogisticRegressionTrainer;
import org.tribuo.data.columnar.FieldProcessor;
import org.tribuo.data.columnar.RowProcessor;
import org.tribuo.data.columnar.processors.field.TextFieldProcessor;
import org.tribuo.data.columnar.processors.response.FieldResponseProcessor;
import org.tribuo.data.csv.CSVDataSource;
import org.tribuo.data.text.impl.TokenPipeline;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.util.tokens.impl.BreakIteratorTokenizer;

public class PriorityModel {

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println(" Smart Grievance Priority ML Training");
        System.out.println("========================================");

        Path datasetPath = Paths.get(
                "src/main/resources/ml/smart_grievance_training_data_1000.csv");

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

        // Priority is the target/output
        FieldResponseProcessor<Label> responseProcessor =
                new FieldResponseProcessor<>(
                        "priority",
                        "UNKNOWN",
                        new LabelFactory());

        RowProcessor<Label> rowProcessor =
                new RowProcessor.Builder<Label>()
                        .setFieldProcessors(fieldProcessors)
                        .build(responseProcessor);

        CSVDataSource<Label> csvSource =
                new CSVDataSource<>(
                        datasetPath,
                        rowProcessor,
                        true);

        System.out.println();
        System.out.println
            ("Dataset loaded successfully.");

        // 80% training, 20% testing
        TrainTestSplitter<Label> splitter =
                new TrainTestSplitter<>(
                        csvSource,
                        0.80,
                        42L);

        MutableDataset<Label> trainData =
                new MutableDataset<>(
                        splitter.getTrain());

        MutableDataset<Label> testData =
                new MutableDataset<>(
                        splitter.getTest());

        System.out.println(
                "Training examples: "
                        + trainData.size());

        System.out.println(
                "Testing examples: "
                        + testData.size());

        System.out.println(
                "Number of priority classes: "
                        + trainData.getOutputIDInfo().size());

        LogisticRegressionTrainer trainer =
                new LogisticRegressionTrainer();

        Model<Label> model =
                trainer.train(trainData);

        System.out.println();
        System.out.println(
                "Priority model trained successfully!");

        // Save the trained priority model
        Path modelPath =
                Paths.get(
                        "src/main/resources/ml/priority-model.tribuo");

        try (OutputStream outputStream =
                     Files.newOutputStream(modelPath)) {

            model.serialize().writeTo(outputStream);
        }

        System.out.println();
        System.out.println("Priority model saved to:");
        System.out.println(
                modelPath.toAbsolutePath());

        System.out.println();
        System.out.println("========================================");
        System.out.println(" PRIORITY MODEL TRAINING COMPLETED");
        System.out.println("========================================");
    }
}
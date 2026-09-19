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
import org.tribuo.classification.evaluation.LabelEvaluation;
import org.tribuo.classification.evaluation.LabelEvaluator;
import org.tribuo.classification.sgd.linear.LogisticRegressionTrainer;
import org.tribuo.data.columnar.FieldProcessor;
import org.tribuo.data.columnar.RowProcessor;
import org.tribuo.data.columnar.processors.field.TextFieldProcessor;
import org.tribuo.data.columnar.processors.response.FieldResponseProcessor;
import org.tribuo.data.csv.CSVDataSource;
import org.tribuo.data.text.impl.TokenPipeline;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.util.tokens.impl.BreakIteratorTokenizer;

public class CategoryModel {

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println(" Smart Grievance Category ML Training");
        System.out.println("========================================");

        Path datasetPath = Paths.get(
                "src/main/resources/ml/smart_grievance_training_data_1000.csv");

        // Create 1-gram and 2-gram text features
        TokenPipeline textPipeline =
                new TokenPipeline(
                        new BreakIteratorTokenizer(Locale.US),
                        2,
                        true);

        // Process grievance text
        ArrayList<FieldProcessor> fieldProcessors =
                new ArrayList<>();

        fieldProcessors.add(
                new TextFieldProcessor(
                        "grievance_text",
                        textPipeline));

        // Category is the target/output
        FieldResponseProcessor<Label> responseProcessor =
                new FieldResponseProcessor<>(
                        "category",
                        "UNKNOWN",
                        new LabelFactory());

        // Build RowProcessor
        RowProcessor<Label> rowProcessor =
                new RowProcessor.Builder<Label>()
                        .setFieldProcessors(fieldProcessors)
                        .build(responseProcessor);

        // Load the complete dataset
        CSVDataSource<Label> csvSource =
                new CSVDataSource<>(
                        datasetPath,
                        rowProcessor,
                        true);

        System.out.println();
        System.out.println(
                "Dataset loaded successfully.");
                        

        // Split into 80% training and 20% testing
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
                "Number of categories: "
                        + trainData.getOutputIDInfo().size());

        // Train the model ONLY on the training set
        LogisticRegressionTrainer trainer =
                new LogisticRegressionTrainer();

        Model<Label> model =
                trainer.train(trainData);

        System.out.println();
        System.out.println(
                "Category model trained successfully!");

        // Evaluate on completely unseen test data
        LabelEvaluator evaluator =
                new LabelEvaluator();

        LabelEvaluation evaluation =
                evaluator.evaluate(
                        model,
                        testData);

        System.out.println();
        System.out.println("========================================");
        System.out.println(" MODEL EVALUATION RESULTS");
        System.out.println("========================================");

        System.out.println(
                LabelEvaluation.toFormattedString(
                        evaluation));

        // Save the model
        Path modelPath =
                Paths.get(
                        "src/main/resources/ml/category-model.tribuo");

        try (OutputStream outputStream =
                     Files.newOutputStream(modelPath)) {

            model.serialize().writeTo(outputStream);
        }

        System.out.println();
        System.out.println("Category model saved to:");
        System.out.println(
                modelPath.toAbsolutePath());

        System.out.println();
        System.out.println("========================================");
        System.out.println(" TRAINING + EVALUATION COMPLETED");
        System.out.println("========================================");
    }
}
/*FraudDetectionModel.java*/
package com.frauddetection.ml;

import com.frauddetection.model.Transaction;
// Lombok annotations removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class FraudDetectionModel {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionModel.class);

    private RandomForest classifier;
    private Instances datasetStructure;

    @PostConstruct
    public void init() {
        try {
            // Initialize the model
            classifier = new RandomForest();

            // Define attributes for the model
            ArrayList<Attribute> attributes = new ArrayList<>();

            // Amount attribute (numeric)
            attributes.add(new Attribute("amount"));

            // Time of day (hour) attribute (numeric)
            attributes.add(new Attribute("hour_of_day"));

            // Known location attribute (nominal)
            ArrayList<String> locationValues = new ArrayList<>(Arrays.asList("known", "unknown"));
            attributes.add(new Attribute("known_location", locationValues));

            // Transaction frequency attribute (numeric)
            attributes.add(new Attribute("recent_transaction_count"));

            // Class attribute (fraud or not)
            ArrayList<String> classValues = new ArrayList<>(Arrays.asList("normal", "fraud"));
            attributes.add(new Attribute("class", classValues));

            // Create dataset structure with attributes
            datasetStructure = new Instances("FraudDetection", attributes, 0);
            datasetStructure.setClassIndex(datasetStructure.numAttributes() - 1);

            // Configure the classifier
            classifier.setNumIterations(100);
            classifier.setMaxDepth(0); // unlimited

            // In a real-world scenario, we would train the model here with historical data
            // For this example, we'll use a pre-trained model or train with sample data
            trainWithSampleData();

            log.info("Fraud detection ML model initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing fraud detection ML model", e);
        }
    }

    private void trainWithSampleData() {
        try {
            // Create sample training data
            Instances trainingData = new Instances(datasetStructure);

            // Add sample instances (in a real system, this would be historical transaction
            // data)
            // Normal transactions
            addInstance(trainingData, 1000.0, 14, "known", 1, "normal");
            addInstance(trainingData, 2000.0, 15, "known", 2, "normal");
            addInstance(trainingData, 3000.0, 16, "known", 1, "normal");
            addInstance(trainingData, 500.0, 12, "known", 1, "normal");
            addInstance(trainingData, 1500.0, 18, "known", 2, "normal");

            // Fraudulent transactions
            addInstance(trainingData, 6000.0, 2, "unknown", 1, "fraud");
            addInstance(trainingData, 7000.0, 3, "unknown", 4, "fraud");
            addInstance(trainingData, 5500.0, 1, "known", 5, "fraud");
            addInstance(trainingData, 8000.0, 4, "unknown", 3, "fraud");
            addInstance(trainingData, 9000.0, 2, "unknown", 4, "fraud");

            // Build the classifier with training data
            classifier.buildClassifier(trainingData);

            log.info("Fraud detection ML model trained with sample data");
        } catch (Exception e) {
            log.error("Error training fraud detection ML model", e);
        }
    }

    private void addInstance(Instances data, double amount, int hourOfDay, String knownLocation,
            int recentTransactionCount, String classValue) {
        double[] values = new double[data.numAttributes()];
        values[0] = amount;
        values[1] = hourOfDay;
        values[2] = data.attribute(2).indexOfValue(knownLocation);
        values[3] = recentTransactionCount;
        values[4] = data.attribute(4).indexOfValue(classValue);

        data.add(new DenseInstance(1.0, values));
    }

    public double predictFraudProbability(Transaction transaction, boolean isKnownLocation,
            int recentTransactionCount) {
        try {
            // Create instance for prediction
            double[] values = new double[datasetStructure.numAttributes()];
            values[0] = transaction.getAmount().doubleValue();
            values[1] = transaction.getTransactionDate().getHour();
            values[2] = datasetStructure.attribute(2).indexOfValue(isKnownLocation ? "known" : "unknown");
            values[3] = recentTransactionCount;

            DenseInstance instance = new DenseInstance(1.0, values);
            instance.setDataset(datasetStructure);

            // Get probability of fraud (class index 1 is "fraud")
            double[] distribution = classifier.distributionForInstance(instance);
            return distribution[1]; // Probability of fraud

        } catch (Exception e) {
            log.error("Error predicting fraud probability", e);
            return 0.0;
        }
    }

    public boolean isSuspiciousTransaction(Transaction transaction, boolean isKnownLocation,
            int recentTransactionCount) {
        double fraudProbability = predictFraudProbability(transaction, isKnownLocation, recentTransactionCount);

        // Consider it suspicious if probability is above 0.6 (60%)
        return fraudProbability > 0.6;
    }
}

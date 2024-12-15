package com.nixtee;

import java.util.HashMap;
import java.util.Map;

public class NixteeClientExample {
    public static void main(String[] args) {
        NixteeClient client = new NixteeClient();

        try {
            // Authenticate and get token
            String token = client.authorize("user@example.com", "password123");
            System.out.println("Token: " + token);

            // Example of using train function
            Map<String, Object> trainData = new HashMap<>();
            trainData.put("data", "your training data here");
            String trainResponse = client.train(trainData);
            System.out.println("Train response: " + trainResponse);

            // Other functions can be used similarly
            // client.save("modelId", data);
            // client.load("modelId");
            // client.predict("modelId", inputData);
            // client.classify("modelId", inputData);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

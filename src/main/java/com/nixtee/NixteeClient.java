package com.nixtee;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NixteeClient {

    private String apiUrl = "https://api.nixtee.com"; // Placeholder API URL
    private String token;

    public NixteeClient(String apiUrl) {
        if (apiUrl != null) {
            this.apiUrl = apiUrl;
        }
    }

    public NixteeClient() {}

    // Authorize function to get JWT token
    public String authorize(String email, String password) throws Exception {
        String url = apiUrl + "/auth/login";
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);

        String response = sendRequest("POST", url, data, null);

        // Assuming response JSON contains a token field
        // e.g., {"token": "your_jwt_token"}
        String token = parseTokenFromResponse(response);
        this.token = token;
        return token;
    }

    // Train function
    public String train(Map<String, Object> modelData) throws Exception {
        return authorizedRequest("POST", "/train", modelData);
    }

    // Save function
    public String save(String modelId, Map<String, Object> data) throws Exception {
        return authorizedRequest("POST", "/models/" + modelId + "/save", data);
    }

    // Load function
    public String load(String modelId) throws Exception {
        return authorizedRequest("GET", "/models/" + modelId + "/load", null);
    }

    // Predict function
    public String predict(String modelId, Map<String, Object> inputData) throws Exception {
        return authorizedRequest("POST", "/models/" + modelId + "/predict", inputData);
    }

    // Classify function
    public String classify(String modelId, Map<String, Object> inputData) throws Exception {
        return authorizedRequest("POST", "/models/" + modelId + "/classify", inputData);
    }

    // Helper method for authorized requests
    private String authorizedRequest(String method, String endpoint, Map<String, Object> data) throws Exception {
        if (this.token == null) {
            throw new IllegalStateException("Authorization token is missing. Please login first.");
        }

        String url = apiUrl + endpoint;
        return sendRequest(method, url, data, "Bearer " + this.token);
    }

    // General HTTP request sender
    private String sendRequest(String method, String urlString, Map<String, ?> data, String authHeader) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        if (authHeader != null) {
            conn.setRequestProperty("Authorization", authHeader);
        }

        if (data != null && !data.isEmpty()) {
            conn.setDoOutput(true);
            String jsonData = mapToJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    // Helper to convert Map to JSON string
    private String mapToJson(Map<String, ?> map) {
        // Basic JSON serialization. You might want to use a library like Jackson or Gson in production.
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            json.append(",");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 1); // Remove trailing comma
        }
        json.append("}");
        return json.toString();
    }

    // Placeholder method to extract token from response
    private String parseTokenFromResponse(String response) {
        // You would parse the JSON response to extract the token field
        // For simplicity, assuming response contains: {"token": "your_jwt_token"}
        return response.substring(response.indexOf(":\"") + 2, response.lastIndexOf("\""));
    }
}

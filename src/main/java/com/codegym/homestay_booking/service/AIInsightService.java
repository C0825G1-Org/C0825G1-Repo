package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.config.ConfigLoader;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIInsightService {
    
    private static final String GEMINI_URL = 
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";
    
    private static final int MAX_RETRIES = 3;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 60000;
    
    /**
     * Generate AI insight from Gemini API with retry logic
     */
    public String generateInsight(String prompt) {
        String apiKey = ConfigLoader.get("gemini.api.key");
        
        if (apiKey == null || apiKey.isEmpty()) {
            return "⚠️ AI Insight không khả dụng: API key chưa được cấu hình.\n\n" +
                   "Vui lòng thêm 'gemini.api.key=YOUR_KEY' vào file config/key.";
        }
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("🤖 AI Insight - Attempt " + attempt + "/" + MAX_RETRIES);
                String result = callGeminiAPI(apiKey, prompt);
                System.out.println("✅ AI Insight - Success on attempt " + attempt);
                return result;
                
            } catch (java.net.SocketTimeoutException e) {
                lastException = e;
                System.err.println("⏱️ Timeout on attempt " + attempt + ": " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    int waitTime = attempt * 3000; // 3s, 6s, 9s
                    System.out.println("⏳ Waiting " + (waitTime/1000) + "s before retry...");
                    try { Thread.sleep(waitTime); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
                
            } catch (Exception e) {
                lastException = e;
                System.err.println("❌ Error on attempt " + attempt + ": " + e.getMessage());
                // For non-timeout errors, check if retryable
                if (e.getMessage() != null && (e.getMessage().contains("503") || e.getMessage().contains("overloaded"))) {
                    if (attempt < MAX_RETRIES) {
                        int waitTime = attempt * 5000; // 5s, 10s, 15s for server errors
                        System.out.println("⏳ Server overloaded. Waiting " + (waitTime/1000) + "s before retry...");
                        try { Thread.sleep(waitTime); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    }
                } else {
                    break;
                }
            }
        }

        String errorMsg = lastException != null ? lastException.getMessage() : "Unknown error";
        if (errorMsg.contains("SocketTimeout") || errorMsg.contains("timed out")) {
            return "⚠️ AI đang phản hồi chậm (timeout sau " + MAX_RETRIES + " lần thử).\n\n" +
                   "Nguyên nhân có thể:\n" +
                   "• Server Gemini đang quá tải\n" +
                   "• Kết nối mạng không ổn định\n\n" +
                   "Vui lòng thử lại sau vài phút.";
        }
        return "⚠️ AI Insight không khả dụng sau " + MAX_RETRIES + " lần thử.\n\nLỗi: " + errorMsg;
    }
    
    /**
     * Call Gemini API with proper timeouts
     */
    private String callGeminiAPI(String apiKey, String prompt) throws Exception {
        URL url = new URL(GEMINI_URL + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String body = "{\n" +
            "  \"contents\": [\n" +
            "    {\n" +
            "      \"parts\": [\n" +
            "        { \"text\": \"" + escapedPrompt + "\" }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        
        // Read response
        int responseCode = conn.getResponseCode();
        
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            throw new Exception("HTTP " + responseCode + ": " + errorResponse.toString());
        }
        
        BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        
        return parseGeminiResponse(response.toString());
    }
    
    /**
     * Parse Gemini API response to extract text
     */
    private String parseGeminiResponse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");
        } catch (Exception e) {
            return "⚠️ Không thể phân tích phản hồi AI.\n\nRaw response: " + json.substring(0, Math.min(200, json.length()));
        }
    }
}

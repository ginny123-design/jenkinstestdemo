package com.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OtpUtil {

    private static final String OTP_URL = "https://qa2.flydata.test4wd.com/api/otp/phone";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String fetchLatestOtp() {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        String latestOtp = "";
        int attemptCount = 0;

        System.out.println("Polling API for latest OTP delta...");
        while (attemptCount < 5) {
            try {
                Thread.sleep(4000);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OTP_URL))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(response.body());
                    JsonNode dataArray = root.path("data");
                    if (dataArray.isArray() && dataArray.size() > 0) {
                        JsonNode newest = dataArray.get(0);
                        String otp = newest.path("otp").asText("");
                        String updatedAtStr = newest.path("updatedAt").asText("");
                        
                        long updatedTime = parseTime(updatedAtStr);
                        long currentTime = System.currentTimeMillis();

                        if (updatedTime > 0 && (currentTime - updatedTime < 40000)) {
                            latestOtp = otp;
                            System.out.println("Acquired fresh OTP via polling: " + latestOtp);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("OTP Polling attempt " + (attemptCount + 1) + " failed: " + e.getMessage());
            }
            attemptCount++;
        }

        if (latestOtp.isEmpty()) {
            System.out.println("Fallback: Timeout polling, attempting to use top value.");
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OTP_URL))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(response.body());
                    JsonNode dataArray = root.path("data");
                    if (dataArray.isArray() && dataArray.size() > 0) {
                        latestOtp = dataArray.get(0).path("otp").asText("");
                    }
                }
            } catch (Exception e) {
                System.err.println("Fallback OTP fetch failed: " + e.getMessage());
            }
        }

        System.out.println("Extracted OTP: " + latestOtp);
        return latestOtp;
    }

    private static long parseTime(String isoString) {
        try {
            return java.time.Instant.parse(isoString).toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }
}

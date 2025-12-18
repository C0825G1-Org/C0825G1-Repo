package com.codegym.homestay_booking.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    
    private static Properties properties = new Properties();
    
    static {
        try {
            boolean loaded = false;
            
            // Try multiple paths to load config
            String[] configPaths = {
                "src/main/java/com/codegym/homestay_booking/config/key",
                "D:\\md4_case_study\\homestay_booking\\src\\main\\java\\com\\codegym\\homestay_booking\\config\\key",
                System.getProperty("user.dir") + "/src/main/java/com/codegym/homestay_booking/config/key",
                System.getProperty("user.dir") + "\\src\\main\\java\\com\\codegym\\homestay_booking\\config\\key"
            };
            
            for (String path : configPaths) {
                try {
                    java.io.File file = new java.io.File(path);
                    if (file.exists()) {
                        try (InputStream input = new FileInputStream(file)) {
                            properties.load(input);
                            System.out.println("✓ Config loaded from: " + path);
                            loaded = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    // Try next path
                }
            }
            
            if (!loaded) {
                System.err.println("⚠ Warning: Could not load config file from any location.");
                System.err.println("  Tried paths:");
                for (String path : configPaths) {
                    System.err.println("  - " + path);
                }
                System.err.println("  AI features will be unavailable.");
            }
            
        } catch (Exception e) {
            System.err.println("⚠ Error loading config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static String get(String key) {
        return properties.getProperty(key, "");
    }
    
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

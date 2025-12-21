package com.codegym.homestay_booking.service;

import com.codegym.homestay_booking.config.VNPayConfig;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VNPay Service for building payment URLs and verifying responses
 */
public class VNPayService {
    
    /**
     * Generate unique transaction reference
     */
    public String generateTxnRef() {
        return "HS" + System.currentTimeMillis();
    }
    
    /**
     * Build VNPay payment URL
     */
    public String createPaymentUrl(String txnRef, long amount, String orderInfo, 
                                   String ipAddress, String returnUrl) {
        
        Map<String, String> params = new TreeMap<>();
        
        params.put("vnp_Version", VNPayConfig.VNP_VERSION);
        params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
        params.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay requires amount x 100
        params.put("vnp_CurrCode", VNPayConfig.VNP_CURRENCY);
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", VNPayConfig.VNP_ORDER_TYPE);
        params.put("vnp_Locale", VNPayConfig.VNP_LOCALE);
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", ipAddress);
        
        // Create date - Use Vietnam timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String createDate = formatter.format(calendar.getTime());
        params.put("vnp_CreateDate", createDate);
        
        // Expire date (15 minutes)
        calendar.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(calendar.getTime());
        params.put("vnp_ExpireDate", expireDate);
        
        // Build query string and hash data
        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();
        
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (value != null && !value.isEmpty()) {
                try {
                    // For hash data: key=URLEncode(value)
                    if (hashData.length() > 0) {
                        hashData.append("&");
                    }
                    hashData.append(key);
                    hashData.append("=");
                    hashData.append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                    
                    // For query string: URLEncode(key)=URLEncode(value)
                    if (query.length() > 0) {
                        query.append("&");
                    }
                    query.append(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()));
                    query.append("=");
                    query.append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Generate secure hash
        String secureHash = hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        
        return VNPayConfig.VNP_PAY_URL + "?" + query.toString();
    }
    
    /**
     * Verify VNPay response signature
     */
    public boolean verifySignature(Map<String, String> params, String receivedHash) {
        // Remove hash params for verification
        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("vnp_SecureHash");
        sortedParams.remove("vnp_SecureHashType");
        
        // Build hash data
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                }
                try {
                    hashData.append(entry.getKey());
                    hashData.append("=");
                    hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        String calculatedHash = hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }
    
    /**
     * HMAC SHA512 hash
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Get client IP address
     */
    public String getIpAddress(javax.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle localhost IPv6
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}

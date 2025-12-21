package com.codegym.homestay_booking.config;

/**
 * VNPay Sandbox Configuration
 * Official test credentials from VNPay documentation
 * Replace with production credentials when deploying
 */
public class VNPayConfig {
    
    // VNPay Sandbox credentials (from VNPay registration email)
    public static final String VNP_TMN_CODE = "DSLJK0DW";
    public static final String VNP_HASH_SECRET = "37CQYBXDY83ZV8Q45RM3VJPUN71GRN3E";
    
    // URLs
    public static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    
    // Version
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_CURRENCY = "VND";
    public static final String VNP_LOCALE = "vn";
    public static final String VNP_ORDER_TYPE = "other";
    
    // Return URL - will be set dynamically based on request
    public static String getReturnUrl(String baseUrl) {
        return baseUrl + "/payment/vnpay-return";
    }
}

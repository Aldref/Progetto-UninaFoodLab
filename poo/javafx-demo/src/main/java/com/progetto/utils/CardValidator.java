package com.progetto.utils;

import java.time.LocalDate;

public class CardValidator {

    // Ritorna il tipo di carta: "Visa", "Mastercard" o "Unknown"
    public static String getCardType(String cardNumber) {
        if (cardNumber == null) return "Unknown";
        if (cardNumber.startsWith("4")) {
            return "Visa";
        }
        if (cardNumber.startsWith("5")) {
            return "Mastercard";
        }
        if (cardNumber.length() >= 4) {
            try {
                String prefix = cardNumber.substring(0, 4);
                int prefixNum = Integer.parseInt(prefix);
                if (prefixNum >= 2221 && prefixNum <= 2720) {
                    return "Mastercard";
                }
            } catch (NumberFormatException e) {
                return "Unknown";
            }
        }
        return "Unknown";
    }

    
    public static boolean isValidCardType(String cardNumber) {
        if (cardNumber == null) return false;
        if (cardNumber.startsWith("4")) {
            return true;
        }
        if (cardNumber.startsWith("5")) {
            return true;
        }
        if (cardNumber.length() >= 4) {
            try {
                String prefix = cardNumber.substring(0, 4);
                int prefixNum = Integer.parseInt(prefix);
                if (prefixNum >= 2221 && prefixNum <= 2720) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isValidExpiryDate(String expiry) {
        try {
            String[] parts = expiry.split("/");
            if (parts.length != 2) {
                return false;
            }
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            if (year < 100) {
                if (year <= 49) {
                    year += 2000;
                } else {
                    year += 1900;
                }
            }
            if (month < 1 || month > 12) {
                return false;
            }
            LocalDate expiryDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
            LocalDate today = LocalDate.now();
            return expiryDate.isAfter(today) || expiryDate.isEqual(today);
        } catch (Exception e) {
            return false;
        }
    }
}
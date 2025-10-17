package com.torqueittask.loan_calc.util;

import java.math.BigDecimal;

public enum AmountLimits {
    LoanAmountLimit;

    // Simple static fields - NO self-reference issue!
    private static final BigDecimal AMOUNT_LIMIT = BigDecimal.valueOf(100000.00);
    private static final String VALIDATION_MSG = "Payment amount cannot exceed loan limit";
    private static BigDecimal currentLimit = AMOUNT_LIMIT; // Mutable for testing

    // Public getters
    public static BigDecimal getAmountLimit() {
        return currentLimit;
    }

    public static String getValidationMsg() {
        return VALIDATION_MSG;
    }

    // For testing - reset limit
    public static void resetForTesting(BigDecimal newLimit) {
        currentLimit = newLimit;
    }
}
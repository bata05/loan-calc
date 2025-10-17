package com.torqueittask.loan_calc.constants;

import java.math.BigDecimal;

public enum AmountLimits {
	LoanAmountLimit(new BigDecimal("1000000.00"), "Payment amount is greater than 1 Million");
	
	private final BigDecimal amountLimit;
    private final String validationMsg;

    AmountLimits(BigDecimal amountLimit, String validationMsg) {
    	this.amountLimit = amountLimit;
    	this.validationMsg = validationMsg;
    }
    
    public BigDecimal getAmountLimit() {
        return amountLimit;
    }

    public String getValidationMsg() {
        return validationMsg;
    }
}

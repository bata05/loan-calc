package com.torqueittask.loan_calc.service;

import java.math.BigDecimal;

import com.torqueittask.loan_calc.exception.AmountExceedException;

public interface LoanCalculatorService {
	BigDecimal calcAccuratePayment(int term, BigDecimal loanAmount, long interestRate,
			BigDecimal residualValue) throws AmountExceedException;
}

package com.torqueittask.loan_calc.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.torqueittask.loan_calc.constants.AmountLimits;
import com.torqueittask.loan_calc.exception.AmountExceedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanCalculatorServiceImpl implements com.torqueittask.loan_calc.service.LoanCalculatorService {

	@Override
	public BigDecimal calcAccuratePayment(int term, BigDecimal loanAmount, long interestRate,
			BigDecimal residualValue) throws AmountExceedException{
		log.info("Performing Loan Accurate Payment: {} term, {} loan amount, {} interest, {} residual value",
					term, loanAmount, interestRate, residualValue);
		BigDecimal actualInterestRate = BigDecimal.valueOf(interestRate).divide(new BigDecimal(100), 5, RoundingMode.HALF_UP);
		BigDecimal interestPerAnnum = actualInterestRate.divide(BigDecimal.valueOf(12), 5, RoundingMode.HALF_UP);
		BigDecimal commonArithmetic = BigDecimal.valueOf(
									  	Math.pow(1 + interestPerAnnum.doubleValue(), term)
									  );
		BigDecimal denom = BigDecimal.valueOf(1).subtract(
													BigDecimal.valueOf(1).divide(
															commonArithmetic, 5, RoundingMode.HALF_UP)
												 );
		BigDecimal paymentDenomitor = denom.divide(interestPerAnnum, 5, RoundingMode.HALF_UP);
		BigDecimal paymentNumerator = loanAmount.subtract(residualValue.divide(
																				BigDecimal.valueOf(
																							Math.pow(1 + interestPerAnnum.doubleValue(), term)
																						), 5, RoundingMode.HALF_UP
																			   )
														 );
		BigDecimal payment = paymentNumerator.divide(paymentDenomitor, 2, RoundingMode.HALF_UP);
		BigDecimal rounded = payment.setScale(2, RoundingMode.HALF_UP);
		if(AmountLimits.LoanAmountLimit.getAmountLimit().compareTo(rounded)<0) {
			log.info("Payment Amount: {} exceeds the Loan Amount: {}",
					payment, AmountLimits.LoanAmountLimit.getValidationMsg());
			throw new AmountExceedException(AmountLimits.LoanAmountLimit.getValidationMsg());
		}
		return rounded;
	}

}

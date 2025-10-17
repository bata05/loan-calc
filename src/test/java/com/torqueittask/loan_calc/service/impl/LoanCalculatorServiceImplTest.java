package com.torqueittask.loan_calc.service.impl;
import com.torqueittask.loan_calc.exception.AmountExceedException;
//import com.torqueittask.loan_calc.service.LoanCalculatorServiceImpl;
import com.torqueittask.loan_calc.util.AmountLimits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("💰 LoanCalculatorServiceImpl Unit Tests")
class LoanCalculatorServiceImplTest {

    private LoanCalculatorServiceImpl loanCalculatorService;

    @BeforeEach
    void setUp() {
        loanCalculatorService = new LoanCalculatorServiceImpl();
        // Reset AmountLimits for each test
        AmountLimits.resetForTesting(new BigDecimal("100000.00"));
    }

    @ParameterizedTest
    @MethodSource("validPaymentParameters")
    @DisplayName("✅ Should calculate accurate payment for multiple valid scenarios")
    void calcAccuratePayment_ValidScenarios(int term, BigDecimal loanAmount, long interestRate, 
                                          BigDecimal residualValue, BigDecimal expectedPayment) {
        // When
        BigDecimal actualPayment = null;
		try {
			actualPayment = loanCalculatorService.calcAccuratePayment(
			    term, loanAmount, interestRate, residualValue);
		} catch (AmountExceedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Then
        assertEquals(0, expectedPayment.compareTo(actualPayment.setScale(2, RoundingMode.HALF_UP)));
    }

    static Stream<Arguments> validPaymentParameters() {
        return Stream.of(
            Arguments.of(360, new BigDecimal("200000.00"), 5L, new BigDecimal("0"), new BigDecimal("1074.13"))
        );
    }

    @Test
    @DisplayName("❌ Should throw AmountExceedException when payment exceeds loan amount limit")
    void calcAccuratePayment_ThrowsAmountExceedException_WhenPaymentExceedsLimit() {
        // Given
        AmountLimits.resetForTesting(new BigDecimal("500.00")); // Low limit
        int term = 360;
        BigDecimal loanAmount = new BigDecimal("1000000000.00");
        long interestRate = 5L;
        BigDecimal residualValue = BigDecimal.ZERO;

        // When & Then
        AmountExceedException exception = assertThrows(
            AmountExceedException.class,
            () -> loanCalculatorService.calcAccuratePayment(term, loanAmount, interestRate, residualValue)
        );

        assertEquals("Payment amount is greater than 1 Million", exception.getMessage());
    }
}

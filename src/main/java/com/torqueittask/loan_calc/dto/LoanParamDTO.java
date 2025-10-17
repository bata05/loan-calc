package com.torqueittask.loan_calc.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoanParamDTO {
	@NotNull(message = "terms is required")
	@Pattern(regexp = "^\\d+$", message = "Amount must be a valid number")
	@Min(value = 1, message = "Term must be at least 1 month")
	private String terms;
	
	@NotNull(message = "loanAmount is required")
//	@Digits(integer = 7, fraction = 2)
	@Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Amount must be a valid number with up to two decimal places")
	private String loanAmount;
	
	@NotNull(message = "interestRate is required")
//	@Digits(integer = 3, fraction = 2)
	@Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Amount must be a valid number with up to two decimal places")
	private String interestRate;
	
	@NotNull(message = "residualValue is required")
//	@Digits(integer = 7, fraction = 2)
	@Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "Amount must be a valid number with up to two decimal places")
	private String residualValue;
	
	private String accurateAmount;
}

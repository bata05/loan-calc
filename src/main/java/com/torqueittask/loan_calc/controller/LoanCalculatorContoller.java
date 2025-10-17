package com.torqueittask.loan_calc.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.torqueittask.loan_calc.dto.LoanParamDTO;
import com.torqueittask.loan_calc.exception.AmountExceedException;
import com.torqueittask.loan_calc.service.LoanCalculatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/loan/api")
@Slf4j
public class LoanCalculatorContoller {
	
	@Autowired
	LoanCalculatorService loanCalculatorService;
	
	@GetMapping("/load")
    public ResponseEntity<LoanParamDTO> getTaskById(HttpSession session) {
		LoanParamDTO loanParam = (LoanParamDTO) session.getAttribute("loanParam");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(loanParam);
    }
	
	@PostMapping("/save")
    public ResponseEntity<LoanParamDTO> save(
    				@Valid @RequestBody LoanParamDTO loanParam, 
    				HttpSession session
    		) throws AmountExceedException{
		// Get session (creates new if none exists)
		BigDecimal loanAmount = new BigDecimal(loanParam.getLoanAmount());
		long interest = Long.parseLong(loanParam.getInterestRate());
		BigDecimal residualValue = new BigDecimal(loanParam.getResidualValue());
		int terms = Integer.parseInt(loanParam.getTerms());
        BigDecimal paymentAmount = loanCalculatorService.calcAccuratePayment(terms, loanAmount, interest, residualValue);
        loanParam.setAccurateAmount(paymentAmount.toString());
        session.setAttribute("loanParam", loanParam);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(loanParam);
	}
}

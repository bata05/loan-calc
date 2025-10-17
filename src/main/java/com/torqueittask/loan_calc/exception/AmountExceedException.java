package com.torqueittask.loan_calc.exception;


public class AmountExceedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AmountExceedException(String message) {
		super(message);
	}
}

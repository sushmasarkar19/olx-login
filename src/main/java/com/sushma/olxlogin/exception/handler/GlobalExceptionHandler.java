package com.sushma.olxlogin.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sushma.olxlogin.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTokenexception(InvalidTokenException ex){
		 ErrorResponse error = new ErrorResponse(ex.getMessage(), 400);
	        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex){
		 ErrorResponse error = new ErrorResponse(ex.getMessage(), 400);
	        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

}

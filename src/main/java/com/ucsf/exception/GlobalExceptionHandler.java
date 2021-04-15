package com.ucsf.exception;

import java.util.Date;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.ucsf.common.ErrorCodes;
import com.ucsf.common.Constants;
import com.ucsf.payload.response.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> resourceNotFoundException(BadCredentialsException ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS.code(),
				Constants.INVALID_CREDENTIALS.errordesc());
		JSONObject errorResponse = new JSONObject();
		errorResponse.put("data", errorDetails);
		return new ResponseEntity<>(errorResponse.toMap(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globleExcpetionHandler(Exception ex, WebRequest request) {
		/*
		 * ErrorDetails errorDetails = new
		 * ErrorDetails(ErrorCodes.INVALID_CREDENTIALS.code(), ex.getMessage(),
		 * request.getDescription(false)); return new ResponseEntity<>(errorDetails,
		 * HttpStatus.INTERNAL_SERVER_ERROR);
		 */
		ErrorResponse errorDetails = new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS.code(),
				Constants.INVALID_CREDENTIALS.errordesc());
		JSONObject errorResponse = new JSONObject();
		errorResponse.put("data", errorDetails);
		return new ResponseEntity<>(errorResponse.toMap(), HttpStatus.NOT_FOUND);
	}
}
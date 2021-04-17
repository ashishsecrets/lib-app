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

	/*
	 * @ExceptionHandler(Exception.class) public ResponseEntity<?>
	 * globleExcpetionHandler(Exception ex, WebRequest request) { JSONObject
	 * errorResponse = new JSONObject(); if
	 * (ex.getMessage().equals("Bad credentials")) { ErrorResponse errorDetails =
	 * new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS.code(),
	 * Constants.INVALID_CREDENTIALS.errordesc()); errorResponse.put("data",
	 * errorDetails); return new ResponseEntity<>(errorResponse.toMap(),
	 * HttpStatus.NOT_FOUND); } else { return new
	 * ResponseEntity<>(errorResponse.toMap(), HttpStatus.NOT_FOUND); } }
	 */
}
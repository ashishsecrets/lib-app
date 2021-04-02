package com.ucsf.service;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {

	public void printLogs(Logger log, String methodName, String message) {
		log.info(""+methodName+"() -> "+message);
	}
	
	public void printErrorLogs(Logger log, String methodName, String message) {
		log.error(""+methodName+"() -> "+message);
	}
}
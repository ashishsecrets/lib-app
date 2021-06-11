package com.ucsf.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.Appointment;
import com.ucsf.payload.request.AppointmentRequest;
import com.ucsf.payload.response.AppointmentResponse;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.AppointmentRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.AppointmentService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.UserService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/appointment")
@Api(tags = "Appointment Controller")
public class AppointmentController {

	private static Logger log = LoggerFactory.getLogger(AppointmentController.class);

	@Autowired
	private LoggerService loggerService;

	@Autowired
	private UserService userService;

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private AppointmentService appointmentservice;
	
	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = "/saveAppointment")
	@ResponseBody
	public ResponseEntity<?> saveAppointment(@RequestBody AppointmentRequest appointmentRequest) {
		User user = null;
		JSONObject responseJson = new JSONObject();
		Appointment appointment = null;
		
		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			if (userDetail != null && userDetail.getUsername() != null) {
				user = userService.findByEmail(userDetail.getUsername());
				loggerService.printLogs(log, "saveAppointment", "Saving appointment by physician: "+user.getId()+" for patient: " + appointmentRequest.getEmail());
			} else {
				loggerService.printErrorLogs(log, "saveAppointment", "Invalid JWT signature.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
						Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
				return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}

			User patient =  userService.findByEmail(appointmentRequest.getEmail());

			if(patient == null) {
				loggerService.printErrorLogs(log, "saveAppointment", "User not found.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.BAD_REQUEST);
			}
			if(appointmentRequest.getGuid() != null && !appointmentRequest.getGuid().isEmpty()) {
				 appointment = appointmentservice.updateAppointment(appointmentRequest, user, patient);
				 responseJson.put("data", new SuccessResponse(true, "Appointment updated successfully."));
				return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.OK);
			}
			else {
				 appointment = appointmentservice.saveAppointment(appointmentRequest, user, patient);			
	             System.out.println(appointment);
				responseJson.put("data", new SuccessResponse(true, "Appointment saved successfully."));
				return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			loggerService.printErrorLogs(log, "saveAppointment", "Error while saving appointment.");
			responseJson.put("error", new ErrorResponse(116, e.getMessage()));
			return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/getAppointments")
	@ResponseBody
	public ResponseEntity<?> getAppointment() {
		User user = null;
		JSONObject responseJson = new JSONObject();
		try {

			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			if (userDetail != null && userDetail.getUsername() != null) {
				user = userService.findByEmail(userDetail.getUsername());
				loggerService.printLogs(log, "getAppointment", "Getting appointments for physician: "+user.getId());
			} else {
				loggerService.printErrorLogs(log, "getAppointment", "Invalid JWT signature.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
						Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
				return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}

			List<Appointment> appointments = appointmentRepository.getAppointmentByPhysicianId(user.getId());
			List<AppointmentResponse> list = new ArrayList<AppointmentResponse>();
			for(Appointment appointment : appointments) {
				AppointmentResponse response = new AppointmentResponse();
				response.setDescription(appointment.getAppointmentDesc());
				response.setTitle(appointment.getAppointmentTitle());
				response.setId(appointment.getAppointmentId());
				response.setStartTime(appointment.getStartDate());
				response.setEndTime(appointment.getEndDate());
				response.setPatient(userRepository.findById(appointment.getUserId()).get().getEmail());
				list.add(response);
				//response.setPatient(appointment.get);
			}
			responseJson.put("data", list);
			return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			loggerService.printErrorLogs(log, "getAppointment", "Error while getting appointments.");
			responseJson.put("error", new ErrorResponse(116, e.getMessage()));
			return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(value = "/deleteAppointment/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
		User user = null;
		JSONObject responseJson = new JSONObject();
		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			if (userDetail != null && userDetail.getUsername() != null) {
				user = userService.findByEmail(userDetail.getUsername());
				loggerService.printLogs(log, "getAppointment", "Getting appointments for physician: "+user.getId());
			} else {
				loggerService.printErrorLogs(log, "getAppointment", "Invalid JWT signature.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
						Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
				return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}
			try {
				appointmentservice.deleteAppointmentById(id,user);
				
				responseJson.put("data", new SuccessResponse(true, "Appointment deleted successfully."));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			//responseJson.put("data", list);
			return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			loggerService.printErrorLogs(log, "getAppointment", "Error while getting appointments.");
			responseJson.put("error", new ErrorResponse(116, e.getMessage()));
			return new ResponseEntity<Object>(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}
	
}

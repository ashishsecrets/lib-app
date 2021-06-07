package com.ucsf.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.controller.AppointmentController;
import com.ucsf.model.Appointment;
import com.ucsf.payload.request.AppointmentRequest;
import com.ucsf.payload.request.Note;
import com.ucsf.repository.AppointmentRepository;
import com.ucsf.service.AppointmentService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.PushNotificationService;

@Service
public class AppointmentServiceImpl implements AppointmentService {
	
	private static Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

	@Autowired
	private LoggerService loggerService;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private PushNotificationService pushNotificationService;

	@Override
	public Appointment saveAppointment(AppointmentRequest appointmentRequest, User physician, User patient) {
		
		Appointment appointment = new Appointment();
		appointment.setStartDate(appointmentRequest.getStartDate());
		appointment.setAppointmentDesc(appointmentRequest.getDescription());
		appointment.setAppointmentTitle(appointmentRequest.getTitle());
		appointment.setEndDate(appointmentRequest.getEndDate());
		appointment.setUserId(patient.getId());
		appointment.setPhysicianId(physician.getId());
		appointmentRepository.save(appointment);

		//send push to patient
		try {
			Note note = new Note();
			note.setContent("Dear " + patient.getFirstName() + "Your appointment has been scheduled for "+ appointment.getStartDate());
			note.setSubject("Appointment Confirmation");
			String msgId = pushNotificationService.sendNotification(note, patient.getDevideId());
			loggerService.printLogs(log, "saveAppointment", "Appointment push notification sent to user: "
					+ patient.getEmail() + "At: " + new Date() + "msgId = " + msgId);
		}catch (Exception e) {
			loggerService.printErrorLogs(log, "saveAppointment",
					"Error while sending appointment push notification to user: "+patient.getEmail()+" At: " + new Date());
		}
		try {
		//send email to patient
			loggerService.printLogs(log, "saveAppointment", "Appointment email notification sent to user: "
					+ patient.getEmail() + "At: " + new Date());
			}catch (Exception e) {
			loggerService.printErrorLogs(log, "saveAppointment",
					"Error while sending appointment email notification to user: "+patient.getEmail()+" At: " + new Date());
		}
		return appointment;
	}

}

package com.ucsf.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.model.Appointment;
import com.ucsf.payload.request.AppointmentRequest;
import com.ucsf.payload.request.Note;
import com.ucsf.repository.AppointmentRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.AppointmentService;
import com.ucsf.service.EmailService;
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

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Appointment saveAppointment(AppointmentRequest appointmentRequest, User physician, User patient) {

		Date startTime = new Date();
		Date endTime = new Date();
		DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
		TimeZone pstTime = TimeZone.getTimeZone("PST");
		pstFormat.setTimeZone(pstTime);
		String startDate = pstFormat.format(appointmentRequest.getStartDate());
		String endDate = pstFormat.format(appointmentRequest.getEndDate());
		try {
			startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").parse(startDate);
			endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").parse(endDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Appointment appointment = new Appointment();
		appointment.setStartDate(startTime);
		appointment.setAppointmentDesc(appointmentRequest.getDescription());
		appointment.setAppointmentTitle(appointmentRequest.getTitle());
		appointment.setEndDate(endTime);
		appointment.setUserId(patient.getId());
		appointment.setPhysicianId(physician.getId());
		appointmentRepository.save(appointment);

		// send push to patient
		try {
			Note note = new Note();
			note.setContent("Dear " + patient.getFirstName() + "Your appointment has been scheduled for "
					+ appointment.getStartDate());
			note.setSubject("Appointment Confirmation");
			String msgId = pushNotificationService.sendNotification(note, patient.getDevideId());
			loggerService.printLogs(log, "saveAppointment", "Appointment push notification sent to user: "
					+ patient.getEmail() + "At: " + new Date() + "msgId = " + msgId);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "saveAppointment",
					"Error while sending appointment push notification to user: " + patient.getEmail() + " At: "
							+ new Date());
		}
		try {
			// send email to patient
			emailService.sendAppointmentEmail(patient.getEmail(), "Appointment has been Scheduled",
					patient.getFirstName() + " " + patient.getLastName(),
					physician.getFirstName() + " " + physician.getLastName(), "scheduled",
					appointment.getStartDate() + " to " + appointment.getEndDate());
			loggerService.printLogs(log, "saveAppointment",
					"Appointment email notification sent to user: " + patient.getEmail() + "At: " + new Date());
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "saveAppointment",
					"Error while sending appointment email notification to user: " + patient.getEmail() + " At: "
							+ new Date());
		}
		return appointment;
	}

	@Override
	public Appointment updateAppointment(AppointmentRequest appointmentRequest, User physician, User patient) {
		Appointment appointment = null;
		Optional<Appointment> appment = appointmentRepository.findById(appointmentRequest.getId());
		if (appment.isPresent()) {
			appointment = appment.get();
			appointment.setStartDate(appointmentRequest.getStartDate());
			appointment.setAppointmentDesc(appointmentRequest.getDescription());
			appointment.setAppointmentTitle(appointmentRequest.getTitle());
			appointment.setEndDate(appointmentRequest.getEndDate());
			appointment.setUserId(patient.getId());
			appointment.setPhysicianId(physician.getId());
			appointmentRepository.save(appointment);
		}

		// send push to patient
		try {
			Note note = new Note();
			note.setContent("Dear " + patient.getFirstName() + "Your appointment has been re-scheduled for "
					+ appointment.getStartDate());
			note.setSubject("Appointment Updated");
			String msgId = pushNotificationService.sendNotification(note, patient.getDevideId());
			loggerService.printLogs(log, "updateAppointment", "Appointment updation push notification sent to user: "
					+ patient.getEmail() + "At: " + new Date() + "msgId = " + msgId);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "updateAppointment",
					"Error while sending appointment push notification to user: " + patient.getEmail() + " At: "
							+ new Date());
		}
		try {
			// send email to patient
			emailService.sendAppointmentEmail(patient.getEmail(), "Appointment has been Re-Scheduled",
					patient.getFirstName() + " " + patient.getLastName(),
					physician.getFirstName() + " " + physician.getLastName(), "updated",
					appointment.getStartDate() + " to " + appointment.getEndDate());
			loggerService.printLogs(log, "updateAppointment",
					"Appointment update email notification sent to user: " + patient.getEmail() + "At: " + new Date());
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "updateAppointment",
					"Error while sending appointment email notification to user: " + patient.getEmail() + " At: "
							+ new Date());
		}
		return appointment;
	}

	@Override
	public void deleteAppointmentById(Long id, User physician) {
		Optional<Appointment> appointment = appointmentRepository.findById(id);
		Appointment existed = appointment.get();
		User patient = userRepository.findById(existed.getUserId()).get();
		try {
			Note note = new Note();
			note.setContent("Dear " + patient.getFirstName() + "Your appointment has been cancelled for "
					+ existed.getStartDate());
			note.setSubject("Appointment Updated");
			String msgId = pushNotificationService.sendNotification(note, patient.getDevideId());
			loggerService.printLogs(log, "updateAppointment",
					"Appointment cancellation push notification sent to user: " + patient.getEmail() + "At: "
							+ new Date() + "msgId = " + msgId);
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "cancelAppointment",
					"Error while sending appointment push notification to user: " + patient.getEmail() + " At: "
							+ new Date());
		}
		try {
			// send email to patient
			emailService.sendAppointmentEmail(patient.getEmail(), "Appointment has been Cancelled",
					patient.getFirstName() + " " + patient.getLastName(),
					physician.getFirstName() + " " + physician.getLastName(), "cancelled",
					existed.getStartDate() + " to " + existed.getEndDate());
			loggerService.printLogs(log, "updateAppointment",
					"Appointment cancel email notification sent to user: " + patient.getEmail() + "At: " + new Date());
		} catch (Exception e) {
			loggerService.printErrorLogs(log, "updateAppointment",
					"Error while sending appointment cancellation email notification to user: " + patient.getEmail()
							+ " At: " + new Date());
		}
		appointmentRepository.deleteById(id);
	}

}

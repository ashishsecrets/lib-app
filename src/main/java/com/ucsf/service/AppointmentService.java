package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.model.Appointment;
import com.ucsf.payload.request.AppointmentRequest;

public interface AppointmentService {

	Appointment saveAppointment(AppointmentRequest appointmentRequest, User user, User patient);

}

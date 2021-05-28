package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.payload.request.AppointmentRequest;

public interface AppointmentService {

	void saveAppointment(AppointmentRequest appointmentRequest, User user, User patient);

}

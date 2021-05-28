package com.ucsf.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.Appointment;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
	
	List<Appointment> getAppointmentByPhysicianId(Long physicianId);
}

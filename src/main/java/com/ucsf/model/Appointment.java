package com.ucsf.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import lombok.Data;

@Entity
@Table(name = "appointment")
@Data
public class Appointment extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "appointment_id")
	private Long appointmentId;
	
	@Column(name = "physician_id")
	private Long physicianId;
	
	@Column(name = "user_id")
	private Long userId;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User users;	
	
	@Column(name = "patient_email")
	private String patientEmail;
	
	@Column(name = "appointment_title")
	private String appointmentTitle;
	
	@Column(name = "appointment_desc")
	private String appointmentDesc;
	
	@Column(name = "appointment_date")
	private Date appointmentDate;
}

package com.ucsf.auditModel;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.model.Appointment;

import lombok.Data;

@Entity
@Table(name = "appointment_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class AppointmentHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "appointment_id", foreignKey = @ForeignKey(name = "FK_appointment_history_file"))
	private Appointment appointment;

	@Lob
	@Column(name = "appointment_content")
	private String appointmentContent;

	@Lob
	@Column(name = "previous_content")
	private String previousContent;

	@Lob
	@Column(name = "changed_content")
	private String changedContent;

	@CreatedBy
	private String modifiedBy;

	@CreatedDate
	private Date modifiedDate;

	private Action action;

	public AppointmentHistory() {
	}

	public AppointmentHistory(Appointment appointment, Action action, String appointmentContent) {
		this.appointment = appointment;
		this.appointmentContent = appointmentContent;
		this.action = action;
	}

	public AppointmentHistory(Appointment appointment, Action action, String appointmentContent, String previousContent,
			String changedContent) {
		this.appointment = appointment;
		this.appointmentContent = appointmentContent;
		this.action = action;
		this.previousContent = previousContent;
		this.changedContent = changedContent;
	}

}

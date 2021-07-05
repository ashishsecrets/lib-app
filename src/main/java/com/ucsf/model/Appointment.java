package com.ucsf.model;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import com.ucsf.entityListener.AppointmentEntityListener;
import lombok.Data;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "appointment")
@EntityListeners(AppointmentEntityListener.class)
@Data
public class Appointment extends Auditable<String> implements Diffable<Appointment> {

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

	@Column(name = "appointment_title")
	private String appointmentTitle;

	@Column(name = "appointment_desc")
	private String appointmentDesc;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	//added comment

	@Override
	public DiffResult diff(Appointment obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("physicianId", this.physicianId, obj.physicianId).append("userId", this.userId, obj.userId)
				.append("appointmentTitle", this.appointmentTitle, obj.appointmentTitle)
				.append("appointmentDesc", this.appointmentDesc, obj.appointmentDesc)
				.build();

	}

}

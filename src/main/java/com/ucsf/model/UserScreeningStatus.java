package com.ucsf.model;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;

import com.ucsf.entityListener.ScreeningAnswersEntityListener;
import com.ucsf.entityListener.UserScreeningStatusEntityListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "user_screening_status")
@EntityListeners(UserScreeningStatusEntityListener.class)
@Data
@NoArgsConstructor
public class UserScreeningStatus extends Auditable<String> implements Diffable<UserScreeningStatus> {

	public enum UserScreenStatus {
		NEWLY_ADDED,INPROGRESS,UNDER_REVIEW, ENROLLED, AVAILABLE, DISQUALIFIED,APPROVED,DISAPPROVED,NOT_ELIGIBLE,TERMS_ACCEPTED
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Long id;

	@Column(name = "user_screening_status")
	private UserScreenStatus userScreeningStatus;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy study;

	@Column(name = "study_id")
	private Long studyId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "index_value")
	private int indexValue;


	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User users;

	@Column(name = "status_updated_date")
	private Date statusUpdatedDate;


	@Override
	public DiffResult<UserScreeningStatus> diff(UserScreeningStatus obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("userScreeningStatus", this.userScreeningStatus, obj.userScreeningStatus)
				.append("studyId", this.studyId, obj.studyId)
				.append("userId", this.userId, obj.userId)
				.append("indexValue", this.indexValue, obj.indexValue)
				.append("statusUpdatedDate", this.statusUpdatedDate, obj.statusUpdatedDate)
				.build();
	}



}

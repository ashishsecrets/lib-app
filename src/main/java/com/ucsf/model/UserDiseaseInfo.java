package com.ucsf.model;

import javax.persistence.*;

import com.ucsf.auth.model.User;

import com.ucsf.entityListener.TasksEntityListener;
import com.ucsf.entityListener.UserDiseaseInfoEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "user_disease_info")
@EntityListeners(UserDiseaseInfoEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class UserDiseaseInfo extends Auditable<String> implements Diffable<UserDiseaseInfo> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "disease_info_id")
	private Long id;

	@Column(name = "height")
	private float height;

	@Column(name = "weight")
	private int weight;
	
	@Column(name = "hospitals")
	private String hospitals;
	
	@Column(name = "doctors")
	private String doctors;
	
	//Other health conditions
	@Column(name = "comorbidities")
	private String comorbidities;
	
	@Column(name = "disease_worsen_factors")
	private String worsenFactors;
	
	@Column(name = "diagonsis")
	private String diagonsis;

	@Column(name = "location")
	private String location;
	
	@Column(name = "family_history")
	private String familyHistory;
	
	@Column(name = "medication_history")
	private String medicationHistory;
	
	@Column(name = "user_id")
	private Long userId;
	
	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_id",insertable = false,updatable = false)
	private User user;

	@Override
	public DiffResult diff(UserDiseaseInfo obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("height", this.height, obj.height)
				.append("weight", this.weight, obj.weight)
				.append("hospitals", this.hospitals, obj.hospitals)
				.append("doctors", this.doctors, obj.doctors)
				.append("comorbidities", this.comorbidities, obj.comorbidities)
				.append("worsenFactors", this.worsenFactors, obj.worsenFactors)
				.append("diagonsis", this.diagonsis, obj.diagonsis)
				.append("location", this.location, obj.location)
				.append("familyHistory", this.familyHistory, obj.familyHistory)
				.append("medicationHistory", this.medicationHistory, obj.medicationHistory)
				.append("userId", this.userId, obj.userId)
				.build();

	}
}

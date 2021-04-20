package com.ucsf.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ucsf.auth.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_disease_info")
@NoArgsConstructor
@Getter
@Setter
public class UserDiseaseInfo extends Auditable<String>{

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

}

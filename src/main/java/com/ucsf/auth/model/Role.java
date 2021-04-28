package com.ucsf.auth.model;

import org.hibernate.annotations.NaturalId;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@NaturalId
	@Column(length = 60)
	private RoleName name;
	
	public Role( RoleName name) {
		this.name= name;
	}
}

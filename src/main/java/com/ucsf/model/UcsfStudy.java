package com.ucsf.model;

import java.util.Date;

import javax.persistence.*;

import com.ucsf.entityListener.TasksEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;


@Entity
@Table(name = "ucsf_studies")
@EntityListeners(TasksEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class UcsfStudy extends Auditable<String> implements Diffable<UcsfStudy> {
	public enum StudyFrequency {
		DAILY, WEEKLY, SEMI_MONTHLY, MONTHLY, QUARTERLY, YEARLY, DAY_OF_MONTH, CUSTOM_DATE
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_id")
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private Boolean enabled;
	
	@Column
	private boolean isDefault;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "frequency")
	private StudyFrequency frequency;
	
	@Column(name = "custom_date")
	private Date custom_date;

	@Override
	public DiffResult diff(UcsfStudy obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("title", this.title, obj.title)
				.append("description", this.description, obj.description)
				.append("isDefault", this.isDefault, obj.isDefault)
				.append("startDate", this.startDate, obj.startDate)
				.append("endDate", this.endDate, obj.endDate)
				.append("custom_date", this.custom_date, obj.custom_date)
				.build();

	}
}

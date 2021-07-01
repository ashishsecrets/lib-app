package com.ucsf.auditModel;

import com.ucsf.auth.model.User;
import com.ucsf.model.ScreeningAnswers;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "screening_answers_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class ScreeningAnswersHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "screening_answer_id", foreignKey = @ForeignKey(name = "FK_screening_answers_history_file"))
	private ScreeningAnswers screeningAnswer;

	@Lob
	@Column(name = "screening_answers_content")
	private String screeningAnswersContent;

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

	public ScreeningAnswersHistory() {
	}

	public ScreeningAnswersHistory(ScreeningAnswers screeningAnswer, Action action, String screeningAnswersContent) {
		this.screeningAnswer = screeningAnswer;
		this.screeningAnswersContent = screeningAnswersContent;
		this.action = action;
	}

	public ScreeningAnswersHistory(ScreeningAnswers screeningAnswer, Action action, String screeningAnswersContent, String previousContent, String changedContent) {
		this.screeningAnswer = screeningAnswer;
		this.screeningAnswersContent = screeningAnswersContent;
		this.action = action;
		this.previousContent = previousContent;
		this.changedContent = changedContent;
	}

}

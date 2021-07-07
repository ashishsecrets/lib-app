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

import com.ucsf.model.Tasks;
import com.ucsf.model.UcsfSurvey;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.model.Appointment;

import lombok.Data;

@Entity
@Table(name = "ucsf_survey_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UcsfSurveyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "survey_id", foreignKey = @ForeignKey(name = "FK_ucsf_survey_history_file"))
    private UcsfSurvey ucsfSurvey;

    @Lob
    @Column(name = "ucsf_survey_content")
    private String ucsfSurveyContent;

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

    public UcsfSurveyHistory() {
    }

    public UcsfSurveyHistory(UcsfSurvey ucsfSurvey, Action action, String ucsfSurveyContent) {
        this.ucsfSurvey = ucsfSurvey;
        this.ucsfSurveyContent = ucsfSurveyContent;
        this.action = action;
    }

    public UcsfSurveyHistory(UcsfSurvey ucsfSurvey, Action action, String ucsfSurveyContent, String previousContent,
                        String changedContent) {
        this.ucsfSurvey = ucsfSurvey;
        this.ucsfSurveyContent = ucsfSurveyContent;
        this.action = action;
        this.previousContent = previousContent;
        this.changedContent = changedContent;
    }

}

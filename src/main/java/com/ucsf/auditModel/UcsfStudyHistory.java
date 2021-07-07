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
import com.ucsf.model.UcsfStudy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.model.Appointment;

import lombok.Data;

@Entity
@Table(name = "ucsf_study_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UcsfStudyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(name = "FK_ucsf_study_history_file"))
    private UcsfStudy ucsfStudy;

    @Lob
    @Column(name = "ucsf_study_content")
    private String ucsfStudycontent;

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

    public UcsfStudyHistory() {
    }

    public UcsfStudyHistory(UcsfStudy ucsfStudy, Action action, String ucsfStudycontent) {
        this.ucsfStudy = ucsfStudy;
        this.ucsfStudycontent = ucsfStudycontent;
        this.action = action;
    }

    public UcsfStudyHistory(UcsfStudy ucsfStudy, Action action, String ucsfStudycontent, String previousContent,
                            String changedContent) {
        this.ucsfStudy = ucsfStudy;
        this.ucsfStudycontent = ucsfStudycontent;
        this.action = action;
        this.previousContent = previousContent;
        this.changedContent = changedContent;
    }

}

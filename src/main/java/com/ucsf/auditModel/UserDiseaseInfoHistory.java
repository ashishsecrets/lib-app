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

import com.ucsf.model.UserDiseaseInfo;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.model.Appointment;

import lombok.Data;

@Entity
@Table(name = "user_disease_info_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserDiseaseInfoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "disease_info_id", foreignKey = @ForeignKey(name = "FK_user_disease_info_history_file"))
    private UserDiseaseInfo userDiseaseInfo;

    @Lob
    @Column(name = "user_disease_info_content")
    private String userDiseaseInfoContent;

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

    public UserDiseaseInfoHistory() {
    }

    public UserDiseaseInfoHistory(UserDiseaseInfo userDiseaseInfo, Action action, String userDiseaseInfoContent) {
        this.userDiseaseInfo = userDiseaseInfo;
        this.userDiseaseInfoContent = userDiseaseInfoContent;
        this.action = action;
    }

    public UserDiseaseInfoHistory(UserDiseaseInfo userDiseaseInfo, Action action, String userDiseaseInfoContent, String previousContent,
                              String changedContent) {
        this.userDiseaseInfo = userDiseaseInfo;
        this.userDiseaseInfoContent = userDiseaseInfoContent;
        this.action = action;
        this.previousContent = previousContent;
        this.changedContent = changedContent;
    }

}

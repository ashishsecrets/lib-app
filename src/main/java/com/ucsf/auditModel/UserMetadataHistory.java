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
import com.ucsf.model.UserMetadata;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.model.Appointment;

import lombok.Data;

@Entity
@Table(name = "user_metadata_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserMetadataHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "metadata_id", foreignKey = @ForeignKey(name = "FK_user_metadata_history_file"))
    private UserMetadata userMetadata;

    @Lob
    @Column(name = "user_metadata_content")
    private String userMetadataContent;

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

    public UserMetadataHistory() {
    }

    public UserMetadataHistory(UserMetadata userMetadata, Action action, String userMetadataContent) {
        this.userMetadata = userMetadata;
        this.userMetadataContent = userMetadataContent;
        this.action = action;
    }

    public UserMetadataHistory(UserMetadata userMetadata, Action action, String userMetadataContent, String previousContent,
                        String changedContent) {
        this.userMetadata = userMetadata;
        this.userMetadataContent = userMetadataContent;
        this.action = action;
        this.previousContent = previousContent;
        this.changedContent = changedContent;
    }

}

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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.model.Appointment;

import lombok.Data;

@Entity
@Table(name = "tasks_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class TasksHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tasks_id", foreignKey = @ForeignKey(name = "FK_tasks_history_file"))
    private Tasks tasks;

    @Lob
    @Column(name = "tasks_content")
    private String tasksContent;

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

    public TasksHistory() {
    }

    public TasksHistory(Tasks tasks, Action action, String tasksContent) {
        this.tasks = tasks;
        this.tasksContent = tasksContent;
        this.action = action;
    }

    public TasksHistory(Tasks tasks, Action action, String tasksContent, String previousContent,
                              String changedContent) {
        this.tasks = tasks;
        this.tasksContent = tasksContent;
        this.action = action;
        this.previousContent = previousContent;
        this.changedContent = changedContent;
    }

}

package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import com.ucsf.entityListener.AppointmentEntityListener;
import com.ucsf.entityListener.TasksEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks")
//@EntityListeners(TasksEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class Tasks extends Auditable<String> implements Diffable<Tasks> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tasks_id")
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Integer duration; //duration in weeks

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "study_id")
    private Long studyId;

    @ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", insertable = false, updatable = false)
    @JsonIgnore
    private UcsfStudy ucsfStudy;

    @Override
    public DiffResult diff(Tasks obj) {
        return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("title", this.title, obj.title)
                .append("description", this.description, obj.description)
                .append("duration", this.duration, obj.duration)
                .append("taskType", this.taskType, obj.taskType)
                .append("studyId", this.studyId, obj.studyId)
                .build();

    }

}

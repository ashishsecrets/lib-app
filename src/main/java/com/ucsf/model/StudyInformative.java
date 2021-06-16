package com.ucsf.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "study_information")
@Getter
@Setter
public class StudyInformative extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id")
    private Long id;

    @Column(name = "info_description", columnDefinition = "TEXT")
    private String infoDescription;

    @Column(name = "info_type")
    private String infoType;

    @Column(name = "study_id")
    private Long studyId;

    @Column(name = "index_value")
    private int indexValue;

    @Column(name = "type_title")
    private String typeTitle;

    @Column(name = "type_id")
    private Long typeId;

    @JsonIgnore
    @ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "study_id", insertable = false, updatable = false)
    private UcsfStudy study;

}


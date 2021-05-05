package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name = "study_images")
@Getter
@Setter
public class StudyImages extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "count")
    private Long count;

    @Column(name = "name")
    private String name;

    @Column(name = "study_id")
    private Long studyId;

    @JsonIgnore
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

}

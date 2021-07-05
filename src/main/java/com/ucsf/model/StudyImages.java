package com.ucsf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "study_images")
@Getter
@Setter
public class StudyImages extends Auditable<String> {


    public enum StudyImageType{
        BODYPARTS, AFFECTEDAREAS
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "filename")
    @ElementCollection(targetClass=String.class)
    private List<String> filename;

    @Column(name = "file_description")
    @ElementCollection(targetClass=String.class)
    private List<String> fileDescription;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "image_type")
    private StudyImageType imageType;

    @Column(name = "count")
    private int count;

    @Column(name = "name")
    private String name;

    @Column(name = "study_id")
    private Long studyId;

    /*@JsonIgnore
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;*/

}

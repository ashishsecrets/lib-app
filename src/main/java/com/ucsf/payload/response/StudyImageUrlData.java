package com.ucsf.payload.response;

import lombok.Data;

    @Data
    public class StudyImageUrlData {

        public enum BodyPartType{
            FRONT, BACK, NONE;
        }

        private Long id;
        private String name;
        private int count;
        private String description;
        private BodyPartType type;

        public StudyImageUrlData(Long id, String name, int count, String description, BodyPartType type) {
            this.id = id;
            this.name = name;
            this.count = count;
            this.description = description;
            this.type = type;
        }

    }

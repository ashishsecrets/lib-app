package com.ucsf.payload.response;

import lombok.Data;

    @Data
    public class StudyImageUrlData {

        private int id;
        private String name;
        private String description;

        public StudyImageUrlData(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

    }

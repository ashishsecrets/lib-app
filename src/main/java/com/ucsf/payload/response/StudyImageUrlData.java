package com.ucsf.payload.response;

import lombok.Data;

    @Data
    public class StudyImageUrlData {

        private Long id;
        private String name;
        private int count;
        private String description;

        public StudyImageUrlData(Long id, String name, int count, String description) {
            this.id = id;
            this.name = name;
            this.count = count;
            this.description = description;
        }

    }

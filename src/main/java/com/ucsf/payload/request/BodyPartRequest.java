package com.ucsf.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BodyPartRequest {
    private Long id;
    private String partImage;
    private String description;
}
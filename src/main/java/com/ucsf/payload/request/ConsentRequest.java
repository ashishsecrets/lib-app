package com.ucsf.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConsentRequest {
	private String parentName;
    private String patientName;
    private String date;
    private String age;
    private String parentSignature;
    private String patientSignature;
   
}

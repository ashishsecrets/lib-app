package com.ucsf.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConsentRequest {
	private String parentName;
    private String adoloscentName;
    private String patientName;
   
}

package com.ucsf.payload.response;

import com.ucsf.model.UserMetadata.StudyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyStatusResponse {

	Boolean isSuccess;
	StudyStatus status;

}

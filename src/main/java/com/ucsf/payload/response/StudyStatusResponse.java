package com.ucsf.payload.response;

import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyStatusResponse {

	Boolean isSuccess;
	UserScreenStatus status;

}

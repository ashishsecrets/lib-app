package com.ucsf.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class SurveyListResponse {

	List<SurveyResponse> list;
}

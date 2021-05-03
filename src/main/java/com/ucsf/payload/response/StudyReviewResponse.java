package com.ucsf.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class StudyReviewResponse {

	private List<StudyReviewData> list;
}

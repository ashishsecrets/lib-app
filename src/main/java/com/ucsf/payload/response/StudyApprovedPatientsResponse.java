package com.ucsf.payload.response;

import com.ucsf.auth.model.User;
import lombok.Data;
import java.util.List;

@Data
public class StudyApprovedPatientsResponse {

    private List<User> list;
}
package com.ucsf.payload.response;

import java.util.Date;

import org.json.JSONObject;

import lombok.Data;

@Data
public class HistoryResponse {
	
	private JSONObject changedContent;
	private Date modifiedDate;
	private String action;
	private String modifiedBy;
}

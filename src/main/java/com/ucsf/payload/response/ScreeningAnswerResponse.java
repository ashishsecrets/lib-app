package com.ucsf.payload.response;

public class ScreeningAnswerResponse {

    Boolean isSuccess;
    String responseMessage;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public ScreeningAnswerResponse() {
    }

    public ScreeningAnswerResponse(Boolean isSuccess, String responseMessage) {
        this.isSuccess = isSuccess;
        this.responseMessage = responseMessage;
    }

}

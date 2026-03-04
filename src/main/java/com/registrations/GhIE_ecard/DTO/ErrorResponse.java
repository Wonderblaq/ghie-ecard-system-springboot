package com.registrations.GhIE_ecard.DTO;

public class ErrorResponse {
    private String message;
    private int errorCode;
    private long timeStamp;

    public ErrorResponse(String message, int errorCode){
        this.message = message;
        this. errorCode = errorCode;
        this. timeStamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}

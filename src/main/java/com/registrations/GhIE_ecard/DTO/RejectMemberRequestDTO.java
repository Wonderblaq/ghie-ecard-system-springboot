package com.registrations.GhIE_ecard.DTO;

public class RejectMemberRequestDTO {

    private String memberId;
    private String reason;

    public String getMemberId() {return memberId;}
    public void setMemberId(String memberId) {this.memberId = memberId;}

    public String getReason() {return reason;}

    public void setReason(String reason) {this.reason = reason;}
}

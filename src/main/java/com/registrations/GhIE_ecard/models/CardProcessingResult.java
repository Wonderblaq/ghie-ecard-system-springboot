package com.registrations.GhIE_ecard.models;

public class CardProcessingResult {

    private int successCount;
    private int failureCount;

    public CardProcessingResult(int successCount, int failureCount) {
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }
}

package com.equal_stage_platform.dev.exception;

public class TargetAudienceException extends RuntimeException {
    public TargetAudienceException(String message) {
        super(message);
    }
    
    public TargetAudienceException(String message, Throwable cause) {
        super(message, cause);
    }
}
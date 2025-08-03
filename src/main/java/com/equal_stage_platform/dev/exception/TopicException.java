package com.equal_stage_platform.dev.exception;

public class TopicException extends RuntimeException {
    public TopicException(String message) {
        super(message);
    }
    
    public TopicException(String message, Throwable cause) {
        super(message, cause);
    }
}
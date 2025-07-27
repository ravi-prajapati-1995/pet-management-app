package org.pet.management.exception;

public class ApiCallException extends RuntimeException {
    private final int statusCode;
    public ApiCallException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    @Override
    public final String getMessage() {
        return super.getMessage() + " with status code: "+statusCode;
    }
}

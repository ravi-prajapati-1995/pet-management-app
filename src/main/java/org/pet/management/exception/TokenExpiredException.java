package org.pet.management.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(final String message) {
        super(message);
    }
}

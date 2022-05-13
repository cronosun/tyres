package com.github.cronosun.tyres.core;

public final class TyResException extends RuntimeException {
    public TyResException(String message) {
        super(message);
    }

    public TyResException(String message, Throwable cause) {
        super(message, cause);
    }
}

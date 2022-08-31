package com.projectgalen.lib.utils.errors;

public class InvalidPropertyKeyValuePair extends RuntimeException {
    public InvalidPropertyKeyValuePair() {
    }

    public InvalidPropertyKeyValuePair(String message) {
        super(message);
    }

    public InvalidPropertyKeyValuePair(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPropertyKeyValuePair(Throwable cause) {
        super(cause);
    }

    protected InvalidPropertyKeyValuePair(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

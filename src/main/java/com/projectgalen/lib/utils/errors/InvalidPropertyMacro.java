package com.projectgalen.lib.utils.errors;

public class InvalidPropertyMacro extends RuntimeException {
    public InvalidPropertyMacro() {
        super();
    }

    public InvalidPropertyMacro(String message) {
        super(message);
    }

    public InvalidPropertyMacro(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPropertyMacro(Throwable cause) {
        super(cause);
    }

    protected InvalidPropertyMacro(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

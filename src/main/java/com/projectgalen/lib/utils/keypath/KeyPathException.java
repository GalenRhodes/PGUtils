package com.projectgalen.lib.utils.keypath;

public class KeyPathException extends RuntimeException {
    public KeyPathException() {
        super();
    }

    public KeyPathException(String message) {
        super(message);
    }

    public KeyPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyPathException(Throwable cause) {
        super(cause);
    }
}

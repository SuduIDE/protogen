package org.sudu.protogen;

public class ProtogenException extends RuntimeException {

    public ProtogenException() {
    }

    public ProtogenException(String message) {
        super(message);
    }

    public ProtogenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtogenException(Throwable cause) {
        super(cause);
    }

    public ProtogenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

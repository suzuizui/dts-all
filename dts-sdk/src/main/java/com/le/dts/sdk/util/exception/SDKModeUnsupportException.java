package com.le.dts.sdk.util.exception;

/**
 * Created by luliang on 14/12/29.
 */
public class SDKModeUnsupportException extends RuntimeException {

    public SDKModeUnsupportException() {
        super();
    }

    public SDKModeUnsupportException(String message) {
        super(message);
    }

    public SDKModeUnsupportException(Throwable error) {
        super(error);
    }

    public SDKModeUnsupportException(String message, Throwable error) {
        super(message, error);
    }
}

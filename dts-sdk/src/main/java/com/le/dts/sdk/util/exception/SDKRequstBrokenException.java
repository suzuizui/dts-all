package com.le.dts.sdk.util.exception;

/**
 * Created by luliang on 14/12/24.
 */
public class SDKRequstBrokenException extends  RuntimeException {

    public SDKRequstBrokenException() {
        super();
    }

    public SDKRequstBrokenException(String message) {
        super(message);
    }

    public SDKRequstBrokenException(Throwable error) {
        super(error);
    }

    public SDKRequstBrokenException(String message, Throwable error) {
        super(message, error);
    }
}

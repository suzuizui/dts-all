package com.le.dts.common.exception;

/**
 * Created by Moshan on 14-11-20.
 */
public class DtsException extends RuntimeException {

    public DtsException(String msg, Exception cause) {
        super(msg, cause);
    }

    public DtsException(String msg) {
        super(msg);
    }
}

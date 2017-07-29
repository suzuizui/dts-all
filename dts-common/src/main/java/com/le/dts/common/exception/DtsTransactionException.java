package com.le.dts.common.exception;

import com.le.dts.common.domain.RunningJob;

/**
 * Created by luliang on 14/12/10.
 */
public class DtsTransactionException extends RuntimeException {

    public DtsTransactionException() {
        super();
    }

    public DtsTransactionException(String message) {
        super(message);
    }

    public DtsTransactionException(Throwable error) {
        super(error);
    }

    public DtsTransactionException(String message, Throwable error) {
        super(message, error);
    }
}

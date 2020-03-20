package org.centerm.Tollway.core;

/**
 * Created by torch on 2018/3/2.
 */

public class CommunicateException extends RuntimeException {
    private int errCode;

    public CommunicateException(int errCode) {
        this.errCode = errCode;
    }

    public CommunicateException(int errCode, String detailMessage) {
        super(detailMessage);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}

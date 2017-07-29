package com.le.dts.sdk;

/**
 * Created by luliang on 14/12/30.
 */
public enum SDKMode {

    DAILY_MODE("daily"),

    ONLINE_MODE("online");

    private SDKMode(String mode) {
        this.mode = mode;
    }

    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}

package com.ym.mybatis.enums;

public enum MessageCode {

    SUCCESS(200, "success"),
    ERROR(500, "error"),
    ;

    private int code;
    private String msg;

    private MessageCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}

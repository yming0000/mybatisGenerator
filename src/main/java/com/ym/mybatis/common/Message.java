package com.ym.mybatis.common;

import com.ym.mybatis.enums.MessageCode;
import lombok.Data;

@Data
public class Message<T> {

    private boolean success = true;
    private int code = 200;
    private String message = "success";
    private T data;

    public Message() {

    }

    public Message(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Message(boolean success) {
        this.success = success;
    }


    public Message(T data) {
        this.data = data;
    }

    public Message(MessageCode messageCode) {
        this.code = messageCode.getCode();
        this.message = messageCode.getMsg();
    }

    public Message(MessageCode messageCode, T data) {
        this.code = messageCode.getCode();
        this.message = messageCode.getMsg();
        this.data = data;
    }

    public Message(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Message(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Message successMessage() {
        return new Message<T>();
    }

    public static <T> Message returnMessage(boolean success, int code, String message, T data) {
        return new Message<T>(success, code, message, data);
    }

    public static <T> Message returnMessage(boolean success) {
        return new Message<T>(success);
    }

    public static <T> Message successMessage(T data) {
        return new Message<T>(data);
    }

    public static <T> Message returnMessage(MessageCode messageCode) {
        return new Message(messageCode);
    }

    public static <T> Message returnMessage(MessageCode messageCode, T data) {
        return new Message(messageCode, data);
    }

    public static <T> Message returnMessage(int code, String message) {
        return new Message(code, message);
    }

    public static <T> Message returnMessage(int code, String message, T data) {
        return new Message(code, message, data);
    }

}
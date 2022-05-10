package com.alishangtian.network.exception;

public class NoMoreChannelException extends Exception {
    public NoMoreChannelException(String msg) {
        super(msg);
    }

    public NoMoreChannelException() {
        this("no more active channel");
    }
}

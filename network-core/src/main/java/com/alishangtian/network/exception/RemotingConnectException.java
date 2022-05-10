package com.alishangtian.network.exception;


public class RemotingConnectException extends RemotingException {
    private static final long serialVersionUID = -5565366231695911316L;

    public RemotingConnectException(String addr) {
        super(addr);
    }

    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to " + addr + " failed", cause);
    }
}

package com.alishangtian.network.exception;


public class RemotingCommandException extends RemotingException {

    public RemotingCommandException(String message) {
        super(message, null);
    }

    public RemotingCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}

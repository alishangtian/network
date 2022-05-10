package com.alishangtian.network;


public interface RPCHook {
    void doBeforeRequest(final String remoteAddr, final NetworkCommand request);

    void doAfterResponse(final String remoteAddr, final NetworkCommand request,
                         final NetworkCommand response);
}

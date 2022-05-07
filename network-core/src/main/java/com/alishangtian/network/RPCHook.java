package com.alishangtian.network;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public interface RPCHook {
    void doBeforeRequest(final String remoteAddr, final NetworkCommand request);

    void doAfterResponse(final String remoteAddr, final NetworkCommand request,
                         final NetworkCommand response);
}

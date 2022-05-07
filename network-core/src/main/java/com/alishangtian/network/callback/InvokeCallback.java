package com.alishangtian.network.callback;

import com.alishangtian.network.common.ResponseFuture;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public interface InvokeCallback {
    /**
     * 回调
     *
     * @param responseFuture
     */
    void operationComplete(final ResponseFuture responseFuture);
}

package com.alishangtian.network.processor;

import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.netty.RemotingResponseCallback;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public abstract class AsyncNettyRequestProcessor implements NettyRequestProcessor {

    public void asyncProcessRequest(ChannelHandlerContext ctx, NetworkCommand request, RemotingResponseCallback responseCallback) throws Exception {
        NetworkCommand response = processRequest(ctx, request);
        responseCallback.callback(response);
    }
}

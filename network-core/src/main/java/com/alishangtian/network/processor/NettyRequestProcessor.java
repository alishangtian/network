package com.alishangtian.network.processor;

import com.alishangtian.network.NetworkCommand;
import io.netty.channel.ChannelHandlerContext;

public interface NettyRequestProcessor {
    NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request)
            throws Exception;

    boolean rejectRequest();
}

package com.alishangtian.network.processor;

import com.alishangtian.network.NetworkCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author maoxiaobing
 * @Description
 * @Date 2020/6/2
 * @Param
 * @Return
 */
public interface NettyRequestProcessor {
    NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request)
            throws Exception;

    boolean rejectRequest();
}

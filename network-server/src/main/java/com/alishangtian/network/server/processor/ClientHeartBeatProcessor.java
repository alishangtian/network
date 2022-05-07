package com.alishangtian.network.server.processor;

import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.common.RemotingCommandResultEnums;
import com.alishangtian.network.processor.NettyRequestProcessor;
import com.alishangtian.network.protocol.HeartBeatLoad;
import com.alishangtian.network.server.NetworkServer;
import com.alishangtian.network.util.JSONUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * 心跳
 *
 * @Description ClientHeartBeatProcessor
 * @Date 2020/12/23 下午10:00
 * @Author maoxiaobing
 **/
@Log4j2
public class ClientHeartBeatProcessor implements NettyRequestProcessor {
    private NetworkServer networkServer;

    public ClientHeartBeatProcessor(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) {
        this.networkServer.addChannelGroup(ctx.channel(), JSONUtils.parseObject(request.getLoad(), HeartBeatLoad.class));
        return NetworkCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).load("success".getBytes(StandardCharsets.UTF_8)).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}

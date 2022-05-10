package com.alishangtian.network.server.processor;

import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.common.RemotingCommandResultEnums;
import com.alishangtian.network.processor.NettyRequestProcessor;
import com.alishangtian.network.protocol.HeartBeatLoad;
import com.alishangtian.network.protocol.TaskLoad;
import com.alishangtian.network.server.NetworkServer;
import com.alishangtian.network.util.JSONUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;


@Log4j2
public class ClientSendTaskResultProcessor implements NettyRequestProcessor {
    private NetworkServer networkServer;

    public ClientSendTaskResultProcessor(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) {
        this.networkServer.acceptResult(JSONUtils.parseObject(request.getLoad(), TaskLoad.class));
        return NetworkCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}

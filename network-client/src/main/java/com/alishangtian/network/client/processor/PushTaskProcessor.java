package com.alishangtian.network.client.processor;

import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.client.NetworkClient;
import com.alishangtian.network.common.RemotingCommandResultEnums;
import com.alishangtian.network.processor.NettyRequestProcessor;
import com.alishangtian.network.protocol.TaskLoad;
import com.alishangtian.network.util.JSONUtils;
import io.netty.channel.ChannelHandlerContext;

public class PushTaskProcessor implements NettyRequestProcessor {
    private NetworkClient networkClient;

    public PushTaskProcessor(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) {
        TaskLoad load = JSONUtils.parseObject(request.getLoad(), TaskLoad.class);
        this.networkClient.acceptTask(load);
        return NetworkCommand.builder().result(RemotingCommandResultEnums.SUCCESS.getResult()).build();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}

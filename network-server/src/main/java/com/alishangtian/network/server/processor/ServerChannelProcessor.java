package com.alishangtian.network.server.processor;

import com.alishangtian.network.ChannelEventListener;
import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.common.RemotingCommandResultEnums;
import com.alishangtian.network.processor.NettyRequestProcessor;
import com.alishangtian.network.protocol.PingRequestBody;
import com.alishangtian.network.server.NetworkServer;
import com.alishangtian.network.util.JSONUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Description ServerChannelProcessor
 * @Date 2020/6/2 下午7:23
 * @Author maoxiaobing
 **/
@Log4j2
public class ServerChannelProcessor implements ChannelEventListener {
    private NetworkServer networkServer;

    public ServerChannelProcessor(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("channel connected address:{}", remoteAddr);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.info("channel closed address:{}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.info("channel exception address:{}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        log.info("idle event from {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Deprecated
    @Override
    public Channel getChannel(String address) {
        return null;
    }

    @Override
    public void removeChannel(String address) {
    }

    @Override
    public Map<String, Channel> getActiveChannel() {
        return null;
    }

}

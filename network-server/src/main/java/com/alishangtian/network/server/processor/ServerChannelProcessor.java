package com.alishangtian.network.server.processor;

import com.alishangtian.network.ChannelEventListener;
import com.alishangtian.network.server.NetworkServer;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class ServerChannelProcessor implements ChannelEventListener {
    private NetworkServer networkServer;

    public ServerChannelProcessor(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.debug("channel connected address:{}", remoteAddr);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.debug("channel closed address:{}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.debug("channel exception address:{}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        log.debug("idle event from {}", remoteAddr);
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

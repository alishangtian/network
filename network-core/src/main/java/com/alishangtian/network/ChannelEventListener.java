package com.alishangtian.network;

import io.netty.channel.Channel;

import java.util.Map;


public interface ChannelEventListener {
    void onChannelConnect(final String remoteAddr, final Channel channel);

    void onChannelClose(final String remoteAddr, final Channel channel);

    void onChannelException(final String remoteAddr, final Channel channel);

    void onChannelIdle(final String remoteAddr, final Channel channel);

    Channel getChannel(String address);

    void removeChannel(String address);

    Map<String, Channel> getActiveChannel();

}

package com.alishangtian.network.client.processor;

import com.alishangtian.network.ChannelEventListener;
import com.alishangtian.network.client.NetworkClient;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class ClientChannelProcessor implements ChannelEventListener {
    private Map<String, Channel> activeChannel = new ConcurrentHashMap<>();
    private Map<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<>();
    private NetworkClient networkServer;

    public void addCountdownLatch(String hostAddr, CountDownLatch countDownLatch) {
        countDownLatchMap.put(hostAddr, countDownLatch);
    }

    public ClientChannelProcessor(NetworkClient networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        log.info("channel connected address {}", remoteAddr);
        activeChannel.put(remoteAddr, channel);
        CountDownLatch countDownLatch;
        if ((countDownLatch = this.countDownLatchMap.get(remoteAddr)) != null) {
            countDownLatch.countDown();
        }
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        log.warn("channel close address {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        log.error("channel exception address {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        log.error("channel idle address {}", remoteAddr);
        removeChannel(remoteAddr);
    }

    @Override
    public Channel getChannel(String address) {
        return activeChannel.get(address);
    }

    @Override
    public void removeChannel(String address) {
        this.activeChannel.remove(address);
    }

    @Override
    public Map<String, Channel> getActiveChannel() {
        return this.activeChannel;
    }
}

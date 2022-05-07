package com.alishangtian.network.server;

import com.alishangtian.network.common.NetworkHelper;
import com.alishangtian.network.common.NetworkUtil;
import com.alishangtian.network.common.RequestCode;
import com.alishangtian.network.common.config.ServerConfig;
import com.alishangtian.network.config.NettyServerConfig;
import com.alishangtian.network.netty.NettyRemotingServer;
import com.alishangtian.network.protocol.HeartBeatLoad;
import com.alishangtian.network.server.processor.ClientHeartBeatProcessor;
import com.alishangtian.network.server.processor.ServerChannelProcessor;
import com.alishangtian.network.util.JSONUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.redis.RedisEncoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Log4j2
public class NetworkServer {

    /**
     * field
     */
    @Autowired
    private NettyServerConfig nettyServerConfig;
    @Autowired
    private ServerConfig serverConfig;

    private NettyRemotingServer server;
    private ServerChannelProcessor serverChannelProcessor;

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE = PROCESSORS;
    private static final int MAX_SIZE = CORE_SIZE + 4;
    private static final int MIN_WORKER_THREAD_COUNT = 8;
    private static final int MIN_SCHEDULE_WORKER_THREAD_COUNT = 4;
    private final Map<String, HashMap<String, Channel>> clients = new HashMap<>();
    private final ReentrantLock clientsLock = new ReentrantLock();
    /**
     * 本机地址
     */
    private String hostAddress;

    private ExecutorService executorService = new ThreadPoolExecutor(Math.max(CORE_SIZE, MIN_WORKER_THREAD_COUNT), Math.max(MAX_SIZE, MIN_WORKER_THREAD_COUNT), 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024), new ThreadFactory() {
        final AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "network-processor-pool-thread-" + num.getAndIncrement());
        }
    });

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Math.max(CORE_SIZE, MIN_SCHEDULE_WORKER_THREAD_COUNT), new ThreadFactory() {
        AtomicLong num = new AtomicLong();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "network-schedule-pool-thread-" + num.getAndIncrement());
        }
    });

    public NetworkServer() {
    }

    @PostConstruct
    public void start() {
        this.hostAddress = serverConfig.getHost() + ":" + nettyServerConfig.getListenPort();
        log.info("hostAddress {}", hostAddress);
        serverChannelProcessor = new ServerChannelProcessor(this);
        server = new NettyRemotingServer(nettyServerConfig, serverChannelProcessor);
        server.registerProcessor(RequestCode.CLIENT_HEART_BEAT, new ClientHeartBeatProcessor(this), executorService);
        server.start();
    }

    public void addChannelGroup(Channel channel, HeartBeatLoad heartBeatLoad) {
        clientsLock.lock();
        try {
            HashMap<String, Channel> channels = clients.get(heartBeatLoad.getGroup());
            if (null == channels) {
                channels = new HashMap<>();
                clients.put(heartBeatLoad.getGroup(), channels);
            }
            String remoteAddress = NetworkHelper.parseChannelRemoteAddr(channel);
            Channel channel1 = channels.get(remoteAddress);
            if (null == channel1 || !channel1.isActive()) {
                channels.put(remoteAddress, channel);
            }
            log.info("clients {}", JSONUtils.toJSONString(clients));
        } finally {
            clientsLock.unlock();
        }
    }

}

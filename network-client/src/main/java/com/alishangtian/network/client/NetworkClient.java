package com.alishangtian.network.client;

import com.alishangtian.network.ConnectFuture;
import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.client.processor.ClientChannelProcessor;
import com.alishangtian.network.common.RequestCode;
import com.alishangtian.network.common.config.ClientConfig;
import com.alishangtian.network.config.NettyClientConfig;
import com.alishangtian.network.exception.RemotingConnectException;
import com.alishangtian.network.exception.RemotingSendRequestException;
import com.alishangtian.network.exception.RemotingTimeoutException;
import com.alishangtian.network.netty.NettyRemotingClient;
import com.alishangtian.network.protocol.HeartBeatLoad;
import com.alishangtian.network.util.JSONUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Log4j2
public class NetworkClient {

    /**
     * field
     */
    @Autowired
    private NettyClientConfig nettyClientConfig;
    @Autowired
    private ClientChannelProcessor clientChannelProcessor;
    @Autowired
    private ClientConfig clientConfig;

    private NettyRemotingClient client;

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE = PROCESSORS;
    private static final int MAX_SIZE = CORE_SIZE + 4;
    private static final int MIN_WORKER_THREAD_COUNT = 8;
    private static final int MIN_SCHEDULE_WORKER_THREAD_COUNT = 4;

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

    @PostConstruct
    public void start() {
        client = new NettyRemotingClient(nettyClientConfig, clientChannelProcessor);
        client.start();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> heartBeat(), 5000L, 5000L, TimeUnit.MILLISECONDS);
    }

    /**
     * connectHost
     *
     * @param host
     * @throws InterruptedException,RemotingConnectException
     */
    public void connectHost(final String host) throws InterruptedException, RemotingConnectException {
        if (isConnected(host)) {
            return;
        }
        final ConnectFuture connectFuture = ConnectFuture.builder().build();
        this.client.connect(host).addListener(future -> {
            if (future.isSuccess()) {
                this.clientChannelProcessor.addCountdownLatch(host, connectFuture.getCountDownLatch());
            } else {
                connectFuture.connectError(host);
            }
        });
        connectFuture.await();
        if (null != connectFuture.getRemotingConnectException()) {
            throw connectFuture.getRemotingConnectException();
        }
        log.info("connect broker {} success", host);
    }

    public boolean isConnected(String host) {
        return null != this.clientChannelProcessor.getChannel(host) && this.clientChannelProcessor.getChannel(host).isActive();
    }

    /**
     * 心跳
     */
    private void heartBeat() {
        String address = clientConfig.getHost() + ":" + clientConfig.getPort();
        try {
            connectHost(address);
            HeartBeatLoad load = HeartBeatLoad.builder().group(clientConfig.getGroup()).build();
            NetworkCommand result = client.invokeSync(address, NetworkCommand.builder().code(RequestCode.CLIENT_HEART_BEAT).load(JSONUtils.toJSONString(load).getBytes(StandardCharsets.UTF_8)).build(), 5000L);
            log.info("result load is {}", new String(result.getLoad()));
        } catch (InterruptedException e) {
            log.error(e);
        } catch (RemotingConnectException | RemotingSendRequestException | RemotingTimeoutException e) {
            log.error(e);
        }
    }

}

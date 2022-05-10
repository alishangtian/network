package com.alishangtian.network.server;

import com.alishangtian.network.NetworkCommand;
import com.alishangtian.network.common.NetworkHelper;
import com.alishangtian.network.common.RequestCode;
import com.alishangtian.network.common.config.ServerConfig;
import com.alishangtian.network.config.NettyServerConfig;
import com.alishangtian.network.exception.RemotingSendRequestException;
import com.alishangtian.network.exception.RemotingTimeoutException;
import com.alishangtian.network.netty.NettyRemotingServer;
import com.alishangtian.network.protocol.HeartBeatLoad;
import com.alishangtian.network.protocol.TaskLoad;
import com.alishangtian.network.server.constants.Animals;
import com.alishangtian.network.server.processor.ClientHeartBeatProcessor;
import com.alishangtian.network.server.processor.ClientSendTaskResultProcessor;
import com.alishangtian.network.server.processor.ServerChannelProcessor;
import com.alishangtian.network.util.JSONUtils;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.util.resources.ga.LocaleNames_ga;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    /**
     * 注册到此server上的客户端
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> clients = new ConcurrentHashMap<>();
    private final ReentrantLock clientsLock = new ReentrantLock();
    private final List<String> animals = new ArrayList<String>() {{
        add(Animals.dog);
        add(Animals.pig);
        add(Animals.dolphin);
        add(Animals.lion);
        add(Animals.tiger);
    }};
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
        server.registerProcessor(RequestCode.CLIENT_PUSH_TASK_RESULT, new ClientSendTaskResultProcessor(this), executorService);
        server.start();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> randomAnimal(), 5000L, 100000L, TimeUnit.MILLISECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> pushTasks(), 20000L, 60000L, TimeUnit.MILLISECONDS);
    }

    public void addChannelGroup(Channel channel, HeartBeatLoad heartBeatLoad) {
        clientsLock.lock();
        try {
            ConcurrentHashMap<String, Channel> channels = clients.get(heartBeatLoad.getGroup());
            if (null == channels) {
                channels = new ConcurrentHashMap<>();
                clients.put(heartBeatLoad.getGroup(), channels);
            }
            String remoteAddress = NetworkHelper.parseChannelRemoteAddr(channel);
            Channel channel1 = channels.get(remoteAddress);
            if (null == channel1 || !channel1.isActive()) {
                channels.put(remoteAddress, channel);
            }
        } finally {
            clientsLock.unlock();
        }
    }

    /**
     * 接收任务结果
     *
     * @param load
     */
    public void acceptResult(TaskLoad load) {
        log.info("task result is {}", JSONUtils.toJSONString(load));
    }

    private void randomAnimal() {
        Long animalIndex = System.currentTimeMillis() % animals.size();
        String animal = animals.get(animalIndex.intValue());
        System.out.println(animal);
    }

    private void pushTasks() {
        long stmp = System.currentTimeMillis();
        TaskLoad load = TaskLoad.builder()
                .pluginName("plugin_" + stmp)
                .params("" + stmp)
                .taskId("task_" + stmp)
                .groupName("default")
                .callbackAddress(hostAddress)
                .build();
        NetworkCommand networkCommand = NetworkCommand.builder()
                .code(RequestCode.SERVER_PUSH_TASK)
                .load(JSONUtils.toJSONString(load).getBytes(StandardCharsets.UTF_8))
                .build();
        ConcurrentHashMap<String, Channel> groupClients = this.clients.get(load.getGroupName());
        if (groupClients.isEmpty()) {
            log.info("no client for group {}", load.getGroupName());
            return;
        }
        List<Channel> channels = new ArrayList<>(groupClients.values());
        Collections.shuffle(channels);
        Channel channel = channels.stream().findFirst().get();
        try {
            NetworkCommand result = this.server.invokeSync(channel, networkCommand, 5000L);
            if (result.isSuccess()) {
                log.info("push task success to {}", NetworkHelper.parseChannelRemoteAddr(channel));
                return;
            }
            log.error("push task error to {}", NetworkHelper.parseChannelRemoteAddr(channel));
        } catch (InterruptedException e) {
            log.error("pushTasks error {}", e.getMessage(), e);
        } catch (RemotingSendRequestException e) {
            log.error("pushTasks error {}", e.getMessage(), e);
        } catch (RemotingTimeoutException e) {
            log.error("pushTasks error {}", e.getMessage(), e);
        }
    }
}

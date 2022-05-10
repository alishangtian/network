package com.alishangtian.network;

import com.alishangtian.network.callback.InvokeCallback;
import com.alishangtian.network.common.Pair;
import com.alishangtian.network.exception.RemotingSendRequestException;
import com.alishangtian.network.exception.RemotingTimeoutException;
import com.alishangtian.network.exception.RemotingTooMuchRequestException;
import com.alishangtian.network.processor.NettyRequestProcessor;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;


public interface RemotingServer extends RemotingService {
    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    int localListenPort();

    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    NetworkCommand invokeSync(final Channel channel, final NetworkCommand request,
                              final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException;

    void invokeAsync(final Channel channel, final NetworkCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final Channel channel, final NetworkCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;
}

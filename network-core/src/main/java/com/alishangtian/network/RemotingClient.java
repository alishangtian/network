package com.alishangtian.network;

import com.alishangtian.network.callback.InvokeCallback;
import com.alishangtian.network.exception.RemotingConnectException;
import com.alishangtian.network.exception.RemotingSendRequestException;
import com.alishangtian.network.exception.RemotingTimeoutException;
import com.alishangtian.network.exception.RemotingTooMuchRequestException;
import com.alishangtian.network.processor.NettyRequestProcessor;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @Description RemotingClient
 * @Date 2020/6/1 下午8:44
 * @Author maoxiaobing
 **/
public interface RemotingClient extends RemotingService {
    void updateNameServerAddressList(final List<String> addrs);

    List<String> getNameServerAddressList();

    NetworkCommand invokeSync(final String addr, final NetworkCommand request,
                              final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final NetworkCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final String addr, final NetworkCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void setCallbackExecutor(final ExecutorService callbackExecutor);

    ExecutorService getCallbackExecutor();

    boolean isChannelWritable(final String addr);
}

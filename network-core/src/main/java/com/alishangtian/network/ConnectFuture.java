package com.alishangtian.network;

import com.alishangtian.network.exception.RemotingConnectException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectFuture {
    @Builder.Default
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private RemotingConnectException remotingConnectException;

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public void connectError(String host) {
        remotingConnectException = new RemotingConnectException(String.format("connect host %s error", host));
        countDownLatch.countDown();
    }
}

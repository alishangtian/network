package com.alishangtian.network.netty;

import com.alishangtian.network.NetworkCommand;

public interface RemotingResponseCallback {
    void callback(NetworkCommand response);
}

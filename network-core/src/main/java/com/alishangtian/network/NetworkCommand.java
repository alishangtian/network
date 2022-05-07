package com.alishangtian.network;

import com.alishangtian.network.common.RemotingCommandResultEnums;
import com.alishangtian.network.common.NetworkCommandType;
import com.alishangtian.network.util.JSONUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求封装类
 *
 * @Description XtimerCommand
 * @Date 2020/6/2 上午9:45
 * @Author maoxiaobing
 **/
@Data
@Builder
public class NetworkCommand implements Serializable {
    /**
     * two bit low bit mark request/response high bit isoneway or not
     */
    private int flag;
    private int code;
    private String remark;
    private byte[] load;
    private static AtomicLong requestId = new AtomicLong(0);
    @Builder.Default
    private long opaque = requestId.getAndIncrement();
    private int result;
    private String hostAddr;

    public ByteBuffer encode() {
        byte[] bytes = encodeBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer;
    }

    public byte[] encodeBytes() {
        return JSONUtils.toJSONString(this).getBytes(JSONUtils.CHARSET_UTF8);
    }

    public static NetworkCommand decode(final byte[] array) {
        return JSONUtils.parseObject(array, NetworkCommand.class);
    }

    public static NetworkCommand decode(final ByteBuffer byteBuffer) {
        return JSONUtils.parseObject(byteBuffer.array(), NetworkCommand.class);
    }

    public void markOnewayRPC() {
        int bits = 1 << 1;
        this.flag |= bits;
    }

    @JsonIgnore
    public boolean isOnewayRPC() {
        int bits = 1 << 1;
        return (this.flag & bits) == bits;
    }

    @JsonIgnore
    public NetworkCommandType getType() {
        if (this.isResponseType()) {
            return NetworkCommandType.RESPONSE_COMMAND;
        }

        return NetworkCommandType.REQUEST_COMMAND;
    }

    @JsonIgnore
    public boolean isResponseType() {
        int bits = 1 << 0;
        return (this.flag & bits) == bits;
    }

    public NetworkCommand markResponseType() {
        int bits = 1 << 0;
        this.flag |= bits;
        return this;
    }

    public boolean isSuccess() {
        return this.result == RemotingCommandResultEnums.SUCCESS.getResult();
    }

}

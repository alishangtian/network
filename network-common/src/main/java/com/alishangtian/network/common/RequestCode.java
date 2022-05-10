package com.alishangtian.network.common;


/**
 * 请求码
 *
 * @author maoxiaobing
 */
public class RequestCode {
    /**
     * channel保活请求
     */
    public static final int CHANNEL_KEEP_ALIVE_PING_REQUEST = 101;

    /**
     * 客户端心跳请求
     */
    public static final int CLIENT_HEART_BEAT = 301;

    /**
     * 推送任务
     */
    public static final int SERVER_PUSH_TASK = 401;

    /**
     * 推送任务结果
     */
    public static final int CLIENT_PUSH_TASK_RESULT = 501;

}

package com.alishangtian.network.common.config;

import lombok.Data;

/**
 * @Desc ServerConfig
 * @Time 2020/6/23 下午5:41
 * @Author maoxiaobing
 */
@Data
public class ClientConfig {
    private String host;
    private String port;
    private String group;
}

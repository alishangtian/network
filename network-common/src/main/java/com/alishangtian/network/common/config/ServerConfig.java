package com.alishangtian.network.common.config;

import lombok.Data;

/**
 * @Desc ServerConfig
 * @Time 2020/6/23 下午5:41
 * @Author maoxiaobing
 */
@Data
public class ServerConfig {
    private String mode;
    private String host;
    private String clusterNodes;
    private boolean servicePubNotify = true;
}

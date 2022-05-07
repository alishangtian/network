package com.alishangtian.network.server.config;

import com.alishangtian.network.common.config.ServerConfig;
import com.alishangtian.network.config.NettyClientConfig;
import com.alishangtian.network.config.NettyServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description ServerConfiguration
 * @Date 2020/6/2 下午7:08
 * @Author maoxiaobing
 **/
@Configuration
public class ServerConfiguration {
    @ConfigurationProperties(prefix = "netty.server")
    @Bean
    public NettyServerConfig nettyServerConfig() {
        return new NettyServerConfig();
    }

    @ConfigurationProperties(prefix = "netty.client")
    @Bean
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @ConfigurationProperties(prefix = "network.server")
    @Bean
    public ServerConfig brokerConfig() {
        return new ServerConfig();
    }

}

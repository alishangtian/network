package com.alishangtian.network.client.config;

import com.alishangtian.network.common.config.ClientConfig;
import com.alishangtian.network.config.NettyClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description ServerConfiguration
 * @Date 2020/6/2 下午7:08
 * @Author maoxiaobing
 **/
@Configuration
public class ClientConfiguration {

    @ConfigurationProperties(prefix = "netty.client")
    @Bean
    public NettyClientConfig nettyClientConfig() {
        return new NettyClientConfig();
    }

    @ConfigurationProperties(prefix = "network")
    @Bean
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }

}

package com.equipment.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: JavaTansanlin
 * @Description: 访问其它服务的时候的模板配置
 * @Date: Created in 11:57 2018/8/15
 * @Modified By:
 */
@Configuration
public class ConfigCommon {

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}

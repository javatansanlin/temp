package com.equipment.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: JavaTansanlin
 * @Description: 监听容器
 * @Date: Created in 12:38 2018/8/13
 * @Modified By:
 */
@Configuration
public class RedisMessageListenerContainerConfig {

    /*
     * Redis消息监听器容器
     * 这个容器加载了RedisConnectionFactory和消息监听器
     */
    @Bean
    public RedisMessageListenerContainer configRedisMessageListenerContainer(RedisConnectionFactory connectionFactory,MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

    @Bean
    CountDownLatch latch(){
        return new CountDownLatch(1);
    }

    /*
     * Receiver实例
     */
    @Bean
    Receiver receiver(CountDownLatch latch){
        return new Receiver(latch);
    }

    /*
     * 将Receiver注册为一个消息监听器，并指定消息接收的方法（receiveMessage）
     * 如果不指定消息接收的方法，消息监听器会默认的寻找Receiver中的handleMessage这个方法作为消息接收的方法
     */
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver){
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}

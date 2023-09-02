package com.study.producer.configuration;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TtlSeniorConfig {
    public static final String EXCHANGE_TTL_NAME= "springboot_ttl_exchange";
    public static final String QUEUE_TTL_NAME= "springboot_ttl_queue";
    //1.交换机
    @Bean("springBootTtlExchange")
    public Exchange springBootExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_TTL_NAME).durable(true).build();
    }

    //2.消息队列
    @Bean("springBooTtlQueue")
    public Queue springBootQueue(){
        //x-message-ttl,queue过期时间
        return QueueBuilder.durable(QUEUE_TTL_NAME).withArgument("x-message-ttl",10000).build();
    }

    //3.绑定交换机和消息队列
    @Bean
    public Binding bingQueueExchange(@Qualifier("springBooTtlQueue") Queue queue, @Qualifier("springBootTtlExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("ttl.#").noargs();
    }
}

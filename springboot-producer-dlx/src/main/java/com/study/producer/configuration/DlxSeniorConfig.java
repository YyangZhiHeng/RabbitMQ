package com.study.producer.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DlxSeniorConfig {
    //正常的交换机
    public static final String EXCHANGE_NORMAL_DLX_NAME = "boot_dlx_exchange";
    //死信交换机
    public static final String EXCHANGE_DLX_NAME = "dlx_exchange";
    //正常的队列
    public static final String QUEUE_NORMAL_DLX_NAME = "boot_dlx_queue";
    //死信队列
    public static final String QUEUE_DLX_NAME = "dlx_queue";

    //1.正常的交换机
    @Bean("bootDlxExchange")
    public Exchange bootDlxExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NORMAL_DLX_NAME).durable(true).build();
    }
    //2.死信交换机
    @Bean("dlxExchange")
    public Exchange dlxExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_DLX_NAME).durable(true).build();
    }

    //3.正常的队列绑定死信交换机
    @Bean("bootDlxQueue")
    public Queue bootDlxQueue(){
        HashMap<String, Object> dlxMap = new HashMap<>(16);
        //设置消息过期时间：x-message-ttl
        dlxMap.put("x-message-ttl",10000);
        //设置最大的消息队列数：x-max-length
        dlxMap.put("x-max-length",10);
        //设置死信交换机
        dlxMap.put("x-dead-letter-exchange",DlxSeniorConfig.EXCHANGE_DLX_NAME);
        //设置信息队列的routingKey
        dlxMap.put("x-dead-letter-routing-key","dlx.haha");
        //正常队列绑定死信交换机
        return QueueBuilder.durable(QUEUE_NORMAL_DLX_NAME).withArguments(dlxMap).build();
    }
    //4.死信队列
    @Bean("dlxQueue")
    public  Queue dlxQueue(){
        return QueueBuilder.durable(QUEUE_DLX_NAME).build();
    }

    //5.正常队列绑定正常交换机
    @Bean
    public Binding bindBootDlxQueueExchange(@Qualifier("bootDlxQueue") Queue queue, @Qualifier("bootDlxExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("boot.dlx.#").noargs();
    }

    //6.死信队列绑定死信交换机
    @Bean
    public Binding bindDlxQueueExchange(@Qualifier("dlxQueue") Queue queue, @Qualifier("dlxExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("dlx.#").noargs();
    }
}

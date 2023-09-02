package com.study.producer.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DelaySeniorDelay {
    /**
     * 高级特牲（延迟队列）-配置类ttl+dlx
     * 1．声明正常的队列(boot_delay_queue)和交换机(boot_delay_exchange)
     * 2．声明死信队列(delay_queue)和死信交换机(delay_exchange)
     * 3.正常风列绑定死信交换机
     *      设置两个参数:
     *          x-dead-letter-exchange:死信交换机名称
     *          x- dead-letter-routing-key:发送给死信交换机的routing key
     *          x-message-ttl:设置延迟时间
     * **/
    //正常的交换机
    public static final String EXCHANGE_NORMAL_DELAY_NAME = "boot_delay_exchange";
    //延迟交换机
    public static final String EXCHANGE_DELAY_NAME = "delay_exchange";
    //正常的队列
    public static final String QUEUE_NORMAL_DELAY_NAME = "boot_delay_queue";
    //延迟队列
    public static final String QUEUE_DELAY_NAME = "delay_queue";

    //1.正常的交换机
    @Bean("bootDelayExchange")
    public Exchange bootDelayExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NORMAL_DELAY_NAME).durable(true).build();
    }
    //2.延迟交换机
    @Bean("delayExchange")
    public Exchange dlxDelayExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_DELAY_NAME).durable(true).build();
    }

    //3.正常的队列绑定死信交换机
    @Bean("bootDelayQueue")
    public Queue bootDelayQueue(){
        HashMap<String, Object> dlxMap = new HashMap<>(16);
        //设置消息过期时间：x-message-ttl
        dlxMap.put("x-message-ttl",10000);
        //设置最大的消息队列数：x-max-length
//        dlxMap.put("x-max-length",10);
        //设置死信交换机
        dlxMap.put("x-dead-letter-exchange",DelaySeniorDelay.EXCHANGE_DELAY_NAME);
        //设置信息队列的routingKey
        dlxMap.put("x-dead-letter-routing-key","delay.haha");
        //正常队列绑定死信交换机
        return QueueBuilder.durable(QUEUE_NORMAL_DELAY_NAME).withArguments(dlxMap).build();
    }
    //4.死信队列
    @Bean("delayQueue")
    public  Queue delayQueue(){
        return QueueBuilder.durable(QUEUE_DELAY_NAME).build();
    }

    //5.正常队列绑定正常交换机
    @Bean
    public Binding bindBootDlxQueueExchange(@Qualifier("bootDelayQueue") Queue queue, @Qualifier("bootDelayExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("boot.delay.#").noargs();
    }

    //6.死信队列绑定死信交换机
    @Bean
    public Binding bindDlxQueueExchange(@Qualifier("delayQueue") Queue queue, @Qualifier("delayExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("delay.#").noargs();
    }
}

package com.study.producer.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //0.声明交换机、队列名
    public  static final String EXCHANGE_NAME = "SPRINGBOOT_TOPIC_EXCHANGE";
    public  static final String QUEUE_NAME = "SPRINGBOOT_QUEUE";
    //1.交换机
    @Bean("springBootExchange")
    public Exchange springBootExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    //2.消息队列
    @Bean("springBootQueue")
    public Queue springBootQueue(){
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    //3.绑定交换机和消息队列
    @Bean
    public Binding bingQueueExchange(@Qualifier("springBootQueue") Queue queue,@Qualifier("springBootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();
    }

}

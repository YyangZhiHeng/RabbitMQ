package com.study.producer.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQSeniorConfig {
    //0.声明交换机、队列名
    public  static final String EXCHANGE_SENIOR_NAME = "boot_senior_exchange";
    public  static final String QUEUE_SENIOR_NAME = "boot_senior_queue";
    //1.交换机
    @Bean("bootSeniorExchange")
    public Exchange bootSeniorExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_SENIOR_NAME).durable(true).build();
    }

    //2.消息队列
    @Bean("bootSeniorQueue")
    public Queue bootSeniorQueue(){
        return QueueBuilder.durable(QUEUE_SENIOR_NAME).build();
    }

    //3.绑定交换机和消息队列
    @Bean
    public Binding bingQueueExchange(@Qualifier("bootSeniorQueue") Queue queue,@Qualifier("bootSeniorExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("senior.#").noargs();
    }

}

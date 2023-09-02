package com.study.producer;

import com.study.producer.configuration.TtlSeniorConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootProducerTtlApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    void contextLoads() {
    }

    @Test
    void testTtl(){
        MessagePostProcessor message = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置message消息的过期时间
                message.getMessageProperties().setExpiration("5000");
                return message;
            }
        };
        //发送消息
        for (int i = 0; i < 5; i++) {
            if (i == 5) {
                //单独过期消息
                rabbitTemplate.convertAndSend(TtlSeniorConfig.EXCHANGE_TTL_NAME,"ttl.a","单独过期的消息",message);
            }else {
                //不过期消息
                rabbitTemplate.convertAndSend(TtlSeniorConfig.EXCHANGE_TTL_NAME,"ttl.b","不过期的消息");
            }
        }
    }
}

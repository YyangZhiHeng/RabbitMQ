package com.study.producer;

import com.study.producer.configuration.DlxSeniorConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootProducerDlxApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testDlx(){
        //1.过期时间的死信队列
//        rabbitTemplate.convertAndSend(DlxSeniorConfig.EXCHANGE_NORMAL_DLX_NAME,"boot.dlx.haha","过期时间的死信队列");
        //2.限制长度的死信队列
//        for (int i = 0; i < 11; i++) {
//            rabbitTemplate.convertAndSend(DlxSeniorConfig.EXCHANGE_NORMAL_DLX_NAME,"boot.dlx.123","限制长度的死信队列");
//        }
        //3.拒绝签收的死信队列
        rabbitTemplate.convertAndSend(DlxSeniorConfig.EXCHANGE_NORMAL_DLX_NAME,"boot.dlx.dddd","拒绝签收死信消息，返回给死信队列");
    }
}

package com.study.producer;

import com.study.producer.configuration.DelaySeniorDelay;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootProducerDelayApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    /**
     * 发送测试消息
     * **/
    @Test
    void testDelay() throws InterruptedException {
        //1.过期时间
        rabbitTemplate.convertAndSend(DelaySeniorDelay.EXCHANGE_NORMAL_DELAY_NAME,"boot.delay.aaa","延迟消息");
        //倒计时
        for (int i = 10; i > 0; i--) {
            Thread.sleep(1000);
            System.out.println("倒计时："+i+"...");
        }
    }
}

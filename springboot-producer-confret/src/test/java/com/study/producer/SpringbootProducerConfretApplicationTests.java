package com.study.producer;

import com.study.producer.configuration.RabbitMQSeniorConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootProducerConfretApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testConfirm() {
        //1.定义一个回调函数
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             CorrelationData: 相关配置信息
             boolean b:exchange交换机是否接收到消息，true/false
             String s: 失败原因
             * */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("confirm模式执行成功");
                if (b) {
                    System.out.println("接受消息成功：" + s);
                } else {
                    System.out.println("接受消息失败：" + s);
                    //怎么处理失败结果
                }
            }
        });
        //2.发送消息
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(RabbitMQSeniorConfig.EXCHANGE_SENIOR_NAME, "senior.ss", "confirm......");
        }
//        rabbitTemplate.convertAndSend(RabbitMQSeniorConfig.EXCHANGE_SENIOR_NAME, "senior.ss", "confirm......");
//        rabbitTemplate.convertAndSend("errorExchange","senior.ss","confirm......");
    }

    /**
     * 1.模拟消息发送成功
     * 控制台打印结果：
     * confirm模式执行成功
     * 接受消息成功：null
     * --------------------------------------------------------------------------------------------
     * 2.模拟消息发送失败：修改了exchange交换机名称
     * 控制台打印结果：
     * confirm模式执行成功
     * 接受消息失败：channel error; protocol method:
     * #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'aaaaa' in vhost '/study', class-id=60, method-id=40)
     */

    @Test
    void testReturn() {
        /**
         回退模式: 当消息发送给Exchange后，Exchange路由Queue失败是才会执行ReturnCallBack步案.
         1.开启回退模式: publisher-returns="true"
         2.设Return CallBack
         3.设置Exchange处塑消息的模式:
            1.如果消息没有路由Queue，则丢弃消息 (默认)
            2.如消息没有路由Queue，返回给消息发送方Return callBack
         * **/
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println("执行return");
                System.out.println(returnedMessage);
            }
        });
        //发送消息
        rabbitTemplate.convertAndSend(RabbitMQSeniorConfig.EXCHANGE_SENIOR_NAME,"senior.aaa","return......");
//                rabbitTemplate.convertAndSend("ErrorExchange","senior","交换机不存在");
        System.out.println("消息发送成功");
    }
}

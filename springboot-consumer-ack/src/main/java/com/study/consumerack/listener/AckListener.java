package com.study.consumerack.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class AckListener implements ChannelAwareMessageListener {
    /***
     * 1.在yaml文件中设置手动签收
     * 2.注解形式： @RabbitListener(queues = "签收的队列名", ackMode = "MANUAL")，MANUAL:为手动签收
     * 3.如果消息签收成功，调用 channel.basicAck(deliveryTag,true);
     * 4.如果签收失败： channel.basicNack(deliveryTag,true);
     * */
    @RabbitListener(queues = "boot_senior_queue", ackMode = "MANUAL")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        Thread.sleep(2000);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //1.接受消息
            System.out.println(new String(message.getBody()));
            //2.处理业务逻辑
            System.out.println("处理业务逻辑");
            //5.模拟签收失败
//            int i = 1/0;
            //3.手动接受消息
            channel.basicAck(deliveryTag,true);
        } catch (Exception e) {
            //4.拒绝签收
            //basicNack(deliveryTag, multiple, requeue);
            /**
             * deliveryTag: 当前收到消息的一个标签
             * multiple: 允许签收多条消息
             * requeue: 是否将消息重返队列，true,消息重返queue,broker会重新发送消息给消费端
             * */

            channel.basicNack(deliveryTag,true,true);
            System.out.println("签收失败，重新签收");
        }
    }
}

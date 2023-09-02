package com.study.consumerack.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class DlxListener implements ChannelAwareMessageListener {
    /***
     * 1.在yaml文件中设置手动签收
     * 2.注解形式： @RabbitListener(queues = "签收的队列名", ackMode = "MANUAL")，MANUAL:为手动签收
     * 3.如果消息签收成功，调用 channel.basicAck(deliveryTag,true);
     * 4.如果签收失败： channel.basicNack(deliveryTag,true);
     * */
    @RabbitListener(queues = "boot_dlx_queue", ackMode = "MANUAL")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
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
             * requeue: 是否将消息重返队列，true,消息重返queue,false,消息返回死信交换机
             * */

            channel.basicNack(deliveryTag,true,false);
            System.out.println("出现异常，签收失败，消息返回死信交换机");
        }
    }
}

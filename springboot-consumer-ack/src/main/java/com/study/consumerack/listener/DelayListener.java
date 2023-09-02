package com.study.consumerack.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class DelayListener implements ChannelAwareMessageListener {

    //监听delay_queue
    @RabbitListener(queues = "delay_queue", ackMode = "MANUAL")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println("处理中...");
        Thread.sleep(2000);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //1.接受消息
            System.out.println(new String(message.getBody()));
            //2.处理业务逻辑
            System.out.println("处理业务逻辑");
            System.out.println("根据订单id查询订单状态...");
            System.out.println("判断是否支付成功...");
            System.out.println("用户未支付，取消订单，回滚库存...");
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
            System.out.println("出现异常，拒绝签收");
        }
    }
}

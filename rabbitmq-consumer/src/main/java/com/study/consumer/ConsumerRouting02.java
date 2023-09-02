package com.study.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerRouting02 {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //2.设置参数
        factory.setHost("localhost");//rabbitmq地址，默认localhost
        factory.setPort(5672);//对外的连接端口，15672是本地登入管理的端口
        factory.setVirtualHost("/study");//创建的虚拟主机
        factory.setUsername("study");//需要连接的用户账号
        factory.setPassword("study");//密码
        //3.创建连接
        Connection connection = factory.newConnection();
        //4.创建Channel
        Channel channel = connection.createChannel();
        //5.创建队列,不写自己创建队列
        /*
        1.队列名称：hello_word
        2.durable:true，是否持久化
        3.exclusive：false，是否独占
        4.autoDelete：false，是否自动删除，没有consumer自动删除
        5.arguments：参数
        *
        * */
        String queuesName2 = "DIRECT_TEST_QUEUE2";
        channel.queueDeclare(queuesName2, true, false, false, null);

        //6.接收消息
        //String queue:队列名称
        // boolean autoAck,：是否自动确认
        // Consumer callback：回调对象
        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                super.handleDelivery(consumerTag, envelope, properties, body);
//                System.out.println("consumerTag:" +consumerTag);
//                System.out.println("getExchange:" +envelope.getExchange());
                System.out.println("getRoutingKey:" + envelope.getRoutingKey());
//                System.out.println("properties:"+properties);
                System.out.println("message:" + new String(body));
                System.out.println("控制台打印日志信息。。。日志级别不为error。。。不写入数据库");
            }
            //consumerTag：标识
            // envelope: 路由key和交换机的信息
            // properties: 配置信息
            // body: 实体消息
        };
        channel.basicConsume(queuesName2, true, consumer);
        System.out.println("成功获取消息");
//        channel.close();
//        connection.close();
    }
}

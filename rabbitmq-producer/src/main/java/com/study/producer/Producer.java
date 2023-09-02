package com.study.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
    public static void main(String[] args) throws Exception {
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
        //5.创建队列
        /*
        1.队列名称：hello_word
        2.durable:true，是否持久化
        3.exclusive：false，是否独占
        4.autoDelete：false，是否自动删除，没有consumer自动删除
        5.arguments：参数
        *
        * */
        channel.queueDeclare("hello_word", true, false, false, null);
        //6.发送消息
        String message = "hello_rabbitmq01";
        /*
        * 1.exchange:交换机名称
        * 2.routingKey：路由名称，与需要路由的队列名称一样
        * 3.props：配置信息
        * 4.body：发送的数据
        * */
        channel.basicPublish("","hello_word",null,message.getBytes());
        System.out.println("发送成功！");
        //7.释放资源
        channel.close();
        connection.close();
    }
}

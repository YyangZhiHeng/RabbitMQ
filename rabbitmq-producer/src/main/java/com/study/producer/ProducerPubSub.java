package com.study.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ProducerPubSub {
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
        /*
         1.String exchange,: 交换机名称
         2.BuiltinExchangeType type: 交换机类型
            DIRECT("direct"),
            FANOUT("fanout"),
            TOPIC("topic"),
            HEADERS("headers");
         3.boolean durable: 是否持久化，是，写入磁盘，否，重启消失
         4.boolean autoDelete：是否自动删除
         5.boolean internal：内部使用，一般false
         6.Map<String, Object> arguments: 参数
         */

        //声明交换机
        String exchangeName = "FANOUT_TEST";
        //5.创建交换机
        channel.exchangeDeclare(exchangeName,BuiltinExchangeType.FANOUT,true,false,null);

        //6.创建队列
//        1.queue: 队列名称：
//        2.durable:true，是否持久化
//        3.exclusive：false，是否独占
//        4.autoDelete：false，是否自动删除，没有consumer自动删除
//        5.arguments：参数
        //声明队列
        String queuesName1 = "FANOUT_TEST_QUEUE1";
        String queuesName2 = "FANOUT_TEST_QUEUE2";
        channel.queueDeclare(queuesName1,true,false,false,null);
        channel.queueDeclare(queuesName2,true,false,false,null);

        //7.绑定队列和交换机
        /*
        1.String queue: 队列名称
        2.String exchange: 交换机名称
        3.String routingKey: 路由键，绑定规则
            交换机为 FANOUT("fanout"),routingKey设置为""
        * */
        channel.queueBind(queuesName1,exchangeName,"");
        channel.queueBind(queuesName2,exchangeName,"");

        //8.发送消息
        /*
        * String exchange：交换机
        * String routingKey：路由key
        * AMQP.BasicProperties props:可以为消息添加状态
        * byte[] body
        * */
        String message = "客户发送了一个请求";
        channel.basicPublish(exchangeName,"",null, message.getBytes());

        //9.释放资源
        channel.close();
        connection.close();
    }
}

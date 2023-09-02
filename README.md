# RabbitMQ

官网：([RabbitMQ:](https://www.rabbitmq.com/))

1. **基本概念：**

   1. **AMQP协议**：AMQP，即 Advanced Message Queuing Protocol (高级消息队列协议)，是一个网络协议，是应用层协议的一个开放标准，为面向消息的中间件设计。基于此协议的客户端与消息中间件可传递消息，并不受客户端/中间件不同产品，不同的开发语言等条件的限制。2006年，AMQP规范发布。类比HTTP。

      ***AMQP架构图：***

      ![1](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/1.png)

      - publisher---消息发布者：生产发布消息

      - exchange---交换机：分发消息

      - routes---路由：交换机分发消息的路径

      - queue--消息队列：接收消息

      - consumer---消息消费者: 消费消息

        ***RabbitMQ架构图：***

        ![2](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/2.png)

        - **Broker:**  接收和分发消息的应用，RabbitMQ Server就是 Message Broker
        - **Virtual host:**  出于多租户和安全因素设计的，把AMQP的基本组件划分到一个虚拟的分组中，类似于网络中的namespace概念。当多个不同的用户使用同一个RabbitMQ server 提供的服务时，可以划分出多个vhost，每个用户在自己的vhost创建exchange / queue等Virtual host: 出于多租户和安全因素设计的，把AMQP的基本组件划分到一个虚拟的分组中，类似于网络中的namespace概念。当多个不同的用户使用同一个RabbitMQ server 提供的服务时，可以划分出多个vhost，每个用户在自己的vhost创建exchange / queue等
        - **Connection:**  publisher / consumer 和broker之间的TCP连接
        - **Channel:**  如果每一次访问RabbitMQ都建立一个Connection，在消息量大的时候建立TCP Connection的开销将是巨大的，效率也较低。Channel是在 connection 内部建立的逻辑连接，如果应用程序支持多线程，通常每个thread创建单独的channel进行通讯，AMQP method包含了channel id 帮助客户端和message broker识别 channel，所以channel之间是完全隔离的。Channel作为轻量级的Connection极大减少了操作系统建立TCP connection的开销
        - **Exchange:**  message,到达 broker的第一站，根据分发规则，匹配查询表中的routing key，分发消息到queue 中去。常用的类型有: direct (point-to-point), topic(publish-subscribe) and fanout (multicast)
        - **Queue:** 消息最终被送到这里等待consumer 取走
        - **Binding:**  exchange和queue 之间的虚拟连接，binding 中可以包含routing key。Binding信息被保存到exchange中的查询表中，用于message 的分发依据

   2. **JMS：** Java消息服务(JavaMessage Service)应用程序接口，是一个Java平台中关于面向消息中间件心的API规范接口

2. **安装：**

   1. 下载Erlang开发环境安装包：[Downloads - Erlang/OTP](https://www.erlang.org/downloads)

      **配置系统变量，不是用户变量**

      ![4](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/4.png)

      ~~~
      %ERLANG_HOME%\bin
      ~~~

      

      ![5](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/5.png)

   2. 下载RabbitMQ：[Releases · rabbitmq/rabbitmq-server (github.com)](https://github.com/rabbitmq/rabbitmq-server/releases/)

3. **默认端口：**http://localhost:15672

   账号：guest

   密码：guest

   

4. **默认**

   ![default](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/default.png)

   4.1 消息生产者：Producer

   ~~~java
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
   ~~~

   

   4.2 消息消费者：

   ~~~java
   public class Consumer {
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
   
           //6.接收消息
           //String queue:队列名称
           // boolean autoAck,：是否自动确认
           // Consumer callback：回调对象
           com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel){
               @Override
               public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
   //                super.handleDelivery(consumerTag, envelope, properties, body);
                   System.out.println("consumerTag:" +consumerTag);
                   System.out.println("getExchange:" +envelope.getExchange());
                   System.out.println("getRoutingKey:" +envelope.getRoutingKey());
                   System.out.println("properties:"+properties);
                   System.out.println("body:" + new String(body));
               }
               //consumerTag：标识
               // envelope: 路由key和交换机的信息
               // properties: 配置信息
               // body: 实体消息
           };
           channel.basicConsume("hello_word",true,consumer);
           System.out.println("成功获取消息");
   //        channel.close();
   //        connection.close();
       }
   }
   ~~~

   

5. **WorkQueues（均分）工作队列：先启动两个消费者，再启动生产者，不然先启动的消费者会把消息直接消费完，多个消费者监听同一个队列**

   ![work_queues](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/work_queues.png)

   1. 生产者：ProducerWorkQueues

      ~~~java
      public class ProducerWorkQueues {
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
              channel.queueDeclare("work_queues", true, false, false, null);
              //6.发送消息
              for (int i = 0; i < 10; i++) {
                  String message = i+":"+"Hello WorkQueues";
                  channel.basicPublish("","work_queues",null,message.getBytes());
              }
      
              /*
              * 1.exchange:交换机名称
              * 2.routingKey：路由名称，与需要路由的队列名称一样
              * 3.props：配置信息
              * 4.body：发送的数据
              * */
      
              System.out.println("发送成功！");
              //7.释放资源
              channel.close();
              connection.close();
          }
      }
      ~~~

      

   2. **消费者：**

      1. 第一个 消费者：ConsumerWorkQueues01

         ~~~  java
         public class ConsumerWorkQueues01 {
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
                 //5.创建队列
                 /*
                 1.队列名称：hello_word
                 2.durable:true，是否持久化
                 3.exclusive：false，是否独占
                 4.autoDelete：false，是否自动删除，没有consumer自动删除
                 5.arguments：参数
                 *
                 * */
                 channel.queueDeclare("work_queues", true, false, false, null);
         
                 //6.接收消息
                 //String queue:队列名称
                 // boolean autoAck,：是否自动确认
                 // Consumer callback：回调对象
                 com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel){
                     @Override
                     public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
         //                super.handleDelivery(consumerTag, envelope, properties, body);
         //                System.out.println("consumerTag:" +consumerTag);
         //                System.out.println("getExchange:" +envelope.getExchange());
                         System.out.println("getRoutingKey:" +envelope.getRoutingKey());
         //                System.out.println("properties:"+properties);
                         System.out.println("body:" + new String(body));
                     }
                     //consumerTag：标识
                     // envelope: 路由key和交换机的信息
                     // properties: 配置信息
                     // body: 实体消息
                 };
                 channel.basicConsume("work_queues",true,consumer);
                 System.out.println("成功获取消息");
         //        channel.close();
         //        connection.close();
             }
         }
         ~~~

         

      2. 第二个消费者：ConsumerWorkQueues02

         ~~~java
         public class ConsumerWorkQueues02 {
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
                 //5.创建队列
                 /*
                 1.队列名称：hello_word
                 2.durable:true，是否持久化
                 3.exclusive：false，是否独占
                 4.autoDelete：false，是否自动删除，没有consumer自动删除
                 5.arguments：参数
                 *
                 * */
                 channel.queueDeclare("work_queues", true, false, false, null);
         
                 //6.接收消息
                 //String queue:队列名称
                 // boolean autoAck,：是否自动确认
                 // Consumer callback：回调对象
                 com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel){
                     @Override
                     public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
         //                super.handleDelivery(consumerTag, envelope, properties, body);
         //                System.out.println("consumerTag:" +consumerTag);
         //                System.out.println("getExchange:" +envelope.getExchange());
                         System.out.println("getRoutingKey:" +envelope.getRoutingKey());
         //                System.out.println("properties:"+properties);
                         System.out.println("body:" + new String(body));
                     }
                     //consumerTag：标识
                     // envelope: 路由key和交换机的信息
                     // properties: 配置信息
                     // body: 实体消息
                 };
                 channel.basicConsume("work_queues",true,consumer);
                 System.out.println("成功获取消息");
         //        channel.close();
         //        connection.close();
             }
         }
         ~~~

6. **publish/subscribe**: **发布订阅模式，队列接受同一条消息，做不同处理，每个消费者监听自己的队列**

   ![publish_subscribe](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/publish_subscribe.png)

   - P(生产者)：也就是要发送消息的程序，但是不再发送到队列中，而是发给X(交换机)

   - X(交换机：Exchang)：接收、转发消息，不具备储存消息的能力，根据交换机类型处理消息

     Fanout：广播，将消息交给所有绑定到交换机的队列

     Direct：定向，把消息交给符合指定routing key 的队列

     Topic：通配符，把消息交给符合routing pattern(路由模式)的队列

     Headers：参数匹配

   - C(消费者)：消费者，消息的接收者，会一直等待消息到来

     

   1. pub/sub消息生产者：

      ~~~java
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
              String message = "Publish/Subscribe";
              channel.basicPublish(exchangeName,"",null, message.getBytes());
      
              //9.释放资源
              channel.close();
              connection.close();
          }
      }
      ~~~

      

   2. pub/sub消息消费者：

      1. 第一个消费者：ConsumerPubSub01

         ~~~java
         public class ConsumerPubSub01 {
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
                 String queuesName1 = "FANOUT_TEST_QUEUE1";
                 channel.queueDeclare("queuesName1", true, false, false, null);
         
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
                         System.out.println("body:" + new String(body));
                         System.out.println("接收到了用户清酒，给用户返回结果");
                     }
                     //consumerTag：标识
                     // envelope: 路由key和交换机的信息
                     // properties: 配置信息
                     // body: 实体消息
                 };
                 channel.basicConsume(queuesName1, true, consumer);
                 System.out.println("成功获取消息");
         //        channel.close();
         //        connection.close();
             }
         }
         ~~~

         

      2. 第二个消费：ConsumerPubSub02

         ~~~java
         public class ConsumerPubSub02 {
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
                 String queuesName2 = "FANOUT_TEST_QUEUE2";
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
                         System.out.println("body:" + new String(body));
                         System.out.println("接收到了用户请求，保存到数据库");
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
         ~~~

   3. **Routing路由模式：**交换机根据RoutingKey，路由到指定的queues里面

      ![Routing](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/Routing.png)

   4. 生产者

      ~~~java
      public class ProducerRouting {
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
              String exchangeName = "DIRECT_TEST";
              //5.创建交换机
              channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true, false, null);
      
              //6.创建队列
      //        1.queue: 队列名称：
      //        2.durable:true，是否持久化
      //        3.exclusive：false，是否独占
      //        4.autoDelete：false，是否自动删除，没有consumer自动删除
      //        5.arguments：参数
              //声明队列
              String queuesName1 = "DIRECT_TEST_QUEUE1";
              String queuesName2 = "DIRECT_TEST_QUEUE2";
      
              channel.queueDeclare(queuesName1, true, false, false, null);
              channel.queueDeclare(queuesName2, true, false, false, null);
      
              //7.绑定队列和交换机
              /*
              1.String queue: 队列名称
              2.String exchange: 交换机名称
              3.String routingKey: 路由键，绑定规则
                  交换机为 FANOUT("fanout"),routingKey设置为""
              * */
              //队列一绑定error写入数据库
              channel.queueBind(queuesName1, exchangeName, "error");
              //队列2绑定error、info、warning控制台打印信息
              channel.queueBind(queuesName2, exchangeName, "info");
              channel.queueBind(queuesName2, exchangeName, "error");
              channel.queueBind(queuesName2, exchangeName, "warning");
      
              //8.发送消息
              /*
               * String exchange：交换机
               * String routingKey：路由key
               * AMQP.BasicProperties props:可以为消息添加状态
               * byte[] body
               * */
              //1.模拟日志级别为info
      //        String message = "模拟日志：调用xxx接口方法。。。控制台打印信息。。。。日志级别为info";
      //        channel.basicPublish(exchangeName, "info", null, message.getBytes());
              //2.模拟日志级别为error
      //        String message = "模拟日志：调用xxx接口方法。。。控制台打印信息。。。。日志级别为error";
      //        channel.basicPublish(exchangeName, "waring", null, message.getBytes());
      
              //3.模拟日志级别为warning
              String message = "模拟日志：调用xxx接口方法。。。控制台打印信息。。。。warning";
              channel.basicPublish(exchangeName, "warning", null, message.getBytes());
              System.out.println("成功发送消息！");
              //9.释放资源
              channel.close();
              connection.close();
          }
      }
      ~~~

      

   5. 消费者

      1. ~~~
         public class ConsumerRouting01 {
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
                 String queuesName1 = "DIRECT_TEST_QUEUE1";
                 channel.queueDeclare(queuesName1, true, false, false, null);
         
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
                         System.out.println("body:" + new String(body));
                         System.out.println("控制台打印日志信息。。。日志级别为error。。。将信息写入数据库");
                     }
                     //consumerTag：标识
                     // envelope: 路由key和交换机的信息
                     // properties: 配置信息
                     // body: 实体消息
                 };
                 channel.basicConsume(queuesName1, true, consumer);
                 System.out.println("成功获取消息");
         //        channel.close();
         //        connection.close();
             }
         }
         ~~~

      2. ~~~java
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
         ~~~

   

   8. **Topics通配符模式：**

   ![topics](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/topics.png)

   - *：一个单词
   - #：多个单词

   1. 生产者

      ~~~
      public class ProducerTopics {
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
      
              //声明交换机名称
              String exchangeName = "TOPIC_TEST";
              //5.创建交换机
              channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, null);
      
              //6.创建队列
      //        1.queue: 队列名称：
      //        2.durable:true，是否持久化
      //        3.exclusive：false，是否独占
      //        4.autoDelete：false，是否自动删除，没有consumer自动删除
      //        5.arguments：参数
              //声明队列
              String queuesName1 = "TOPIC_TEST_QUEUE1";
              String queuesName2 = "TOPIC_TEST_QUEUE2";
      
              channel.queueDeclare(queuesName1, true, false, false, null);
              channel.queueDeclare(queuesName2, true, false, false, null);
      
              //7.绑定队列和交换机
              /*
              1.String queue: 队列名称
              2.String exchange: 交换机名称
              3.String routingKey: 路由键，绑定规则
                  交换机为 FANOUT("fanout"),routingKey设置为""
              * */
              //需求模拟：所有.error结尾和order.开头的数据存入数据库
              channel.queueBind(queuesName1, exchangeName, "#.error");
              channel.queueBind(queuesName1, exchangeName, "order.*");
              //需求模拟：不管什么级别的信息，都打印到控制台中
              channel.queueBind(queuesName2, exchangeName, "*.*");
              //8.发送消息
              /*
               * String exchange：交换机
               * String routingKey：路由key
               * AMQP.BasicProperties props:可以为消息添加状态
               * byte[] body
               * */
              //1.发送order开头的信息,消息队列TOPIC_TEST_QUEUE1绑定两次，TOPIC_TEST_QUEUE2绑定一次
              String message = "模拟日志：调用xxx接口方法。。。控制台打印信息。。。。order.info";
              channel.basicPublish(exchangeName, "order.info", null, message.getBytes());
              System.out.println("成功发送消息！");
              //9.释放资源
              channel.close();
              connection.close();
          }
      }
      ~~~

      

   2. 消费者

      1. ~~~java
         public class ConsumerTopics01 {
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
                 String queuesName1 = "TOPIC_TEST_QUEUE1";
                 channel.queueDeclare(queuesName1, true, false, false, null);
         
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
                         System.out.println("控制台打印日志信息。。。order.info。。。将信息写入数据库");
                     }
                     //consumerTag：标识
                     // envelope: 路由key和交换机的信息
                     // properties: 配置信息
                     // body: 实体消息
                 };
                 channel.basicConsume(queuesName1, true, consumer);
                 System.out.println("成功获取消息");
         //        channel.close();
         //        connection.close();
             }
         }
         ~~~

      2. ~~~java
         public class ConsumerTopics02 {
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
                 String queuesName2 = "TOPIC_TEST_QUEUE2";
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
                         System.out.println("控制台打印日志信息。。。*.*。。。不写入数据库");
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
         ~~~

7. **Spring整合RabbitMQ**

8. **SpringBoot整合RabbitMQ**

   1. 引入依赖

      ~~~java
      <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-amqp</artifactId>
              </dependency>
      ~~~

      

   2. 配置yaml文件

      ~~~java
      spring:
        rabbitmq:
          host: localhost  # ip地址，本地可以默认不写
          port: 5672 # 注册的端口号，不是15672：管理登入的端口
          username: study # 已经创建好的账号
          password: study #密码
          virtual-host: /study # 虚拟机名
      
      ~~~

   3. 生产者

      1. RabbitMQConfig

         ~~~
         package com.study.producer.rabbitmq.config;
         
         import org.springframework.amqp.core.*;
         import org.springframework.beans.factory.annotation.Qualifier;
         import org.springframework.context.annotation.Bean;
         import org.springframework.context.annotation.Configuration;
         
         @Configuration
         public class RabbitMQConfig {
             //0.声明交换机、队列名
             public  static final String EXCHANGE_NAME = "SPRINGBOOT_TOPIC_EXCHANGE";
             public  static final String QUEUE_NAME = "SPRINGBOOT_QUEUE";
             //1.交换机
             @Bean("springBootExchange")
             public Exchange springBootExchange(){
                 return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
             }
         
             //2.消息队列
             @Bean("springBootQueue")
             public Queue springBootQueue(){
                 return QueueBuilder.durable(QUEUE_NAME).build();
             }
         
             //3.绑定交换机和消息队列
             @Bean
             public Binding bingQueueExchange(@Qualifier("springBootQueue") Queue queue,@Qualifier("springBootExchange") Exchange exchange){
                 return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();
             }
         
         }
         ~~~

         ~~~java
             //模拟消息发送
         	@Test
             public void testSendMQ(){
                 rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"boot.aa","hello springboot rabbitmq");
                 System.out.println("发送成功");
             }
         ~~~

         

   4. 消费者

      1. ~~~java
         @Component
         public class RabbitMQListener {
         
             @RabbitListener(queues = "SPRINGBOOT_QUEUE")
             public void listenerQueue(Message message) {
                 System.out.println(new String(message.getBody()));
             }
         }
         ~~~

      2. 

9. **RabbitMQ的高级特性**：[SpringBoot整合RabbitMQ高级特性CSDN博客](https://blog.csdn.net/weixin_45962477/article/details/129063961)

   1. *消息可靠投递原理：*

      1. ~~~
         在使用 RabbitMQQ的时候，作为消息发送方希望杜绝任何消息丢失或者投递失败场景。RabbitMQ 为我们提供了两种方式用来控制消息的投递可靠性模式。
         confirm 确认模式
         return 退回模式
         rabbitmg 整个消息投递的路径为:
         producer--->rabbitmg broker--->exchange--->queue--->consumer
         消息从 producer到 exchange 则会返回一个 confirmCallback
         消息从 exchange-->queue 投递失败则会返回一个 returnCallback我们将利用这两个 callback 控制消息的可靠性投递
         ~~~

      2. confirm 确认模式:  需要外理使用rabbitTemplate.setConfimCallback设置回调函数。当消息发送到exchange后回调conim方法。在方法中判断ack，如果为true，则发送成功，如果为false， 则发送失败，需要处理

         

         1. 创建配置类：RabbitMQSeniorConfig

            ~~~yaml
            spring:
              rabbitmq:
                host: localhost  # ip地址，本地可以默认不写
                port: 5672 # 注册的端口号，不是15672：管理登入的端口
                username: study # 已经创建好的账号
                password: study #密码
                virtual-host: /study # 虚拟机名
                publisher-confirm-type: correlated #开启确定模式
                
            ~~~

            

            ~~~java
            package com.study.producer.configuration;
            
            import org.springframework.amqp.core.*;
            import org.springframework.beans.factory.annotation.Qualifier;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            
            @Configuration
            public class RabbitMQSeniorConfig {
                //0.声明交换机、队列名
                public  static final String EXCHANGE_SENIOR_NAME = "boot_senior_exchange";
                public  static final String QUEUE_SENIOR_NAME = "boot_senior_queue";
                //1.交换机
                @Bean("bootSeniorExchange")
                public Exchange bootSeniorExchange(){
                    return ExchangeBuilder.topicExchange(EXCHANGE_SENIOR_NAME).durable(true).build();
                }
            
                //2.消息队列
                @Bean("bootSeniorQueue")
                public Queue bootSeniorQueue(){
                    return QueueBuilder.durable(QUEUE_SENIOR_NAME).build();
                }
            
                //3.绑定交换机和消息队列
                @Bean
                public Binding bingQueueExchange(@Qualifier("bootSeniorQueue") Queue queue,@Qualifier("bootSeniorExchange") Exchange exchange){
                    return BindingBuilder.bind(queue).to(exchange).with("senior.#").noargs();
                }
            
            }
            ~~~

            

         2. 在测试类中创建测试方法：

            ~~~java
                @Test
                void testConfirm() {
                    //1.定义一个回调函数
                    rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
                        /**
                         CorrelationData: 相关配置信息
                         boolean b(ack):exchange交换机是否接收到消息，true/false
                         String s: 失败原因
                         * */
                        @Override
                        public void confirm(CorrelationData correlationData, boolean b, String s) {
                            System.out.println("confirm模式执行成功");
                            if (b) {
                                System.out.println("接受消息成功：" + s);
                            } else {
                                System.out.println("接受消息失败：" + s);
                            }
                        }
                    });
                    //2.发送消息
                    rabbitTemplate.convertAndSend(RabbitMQSeniorConfig.EXCHANGE_SENIOR_NAME, "senior.ss", "confirm......");
            //        rabbitTemplate.convertAndSend("aaaaa","senior.ss","confirm......");
                }
                /**1.模拟消息发送成功
                 *控制台打印结果：
                 * confirm模式执行成功
                 * 接受消息成功：null
                 * --------------------------------------------------------------------------------------------
                 * 2.模拟消息发送失败：修改了exchange交换机名称
                 * 控制台打印结果：
                 * confirm模式执行成功
                 * 接受消息失败：channel error; protocol method:
                 * #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'aaaaa' in vhost '/study', class-id=60, method-id=40)
                 * */
            }
            ~~~

            

         

      3. return 退回模式：使用rabbitTemolate.setReturnCallback设置退回承数，当消息从exchange路由到queue失败后，如果设置了rabbitTemplate.setMandatory(true)参数，则会将消息退回给producer。并执行回调函数returnedMessage。

         1. 定义一个测试方法

            1. ~~~java
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
                       rabbitTemplate.convertAndSend(RabbitMQSeniorConfig.EXCHANGE_SENIOR_NAME,"error.aaa","return......");
               //                rabbitTemplate.convertAndSend("ErrorExchange","senior","交换机不存在");
                       System.out.println("消息发送成功");
                   }
               ~~~

            2. 

         2. 啊

   2. Comsumer Ack：

      1. 原理：

         1. ~~~
            ack指Acknowledge，确认。表示消费端收到消息后的确认方式。
            有三种确认方式:
            1.自动确认: acknowledge="none"
            2.手动确认: acknowledge="manual"
            3.根据异常情况确认: acknowledge="auto",(这种方式使用麻烦)
            其中自动确认是指，当消息一旦被Consumer接收到，则自动确认收到，并将相应 message 从 RabbitMQ的消息缓存中移除。但是在实际业务处理中，
            很可能消息接收到，业务处理出现异常，那么该消息就会丢失。如果设置了手动确认方式，则需要在业务处理成功后，调用channel.basicAck()，手动签收，如果出现异常，则调用channel.basicNack()方法，让其自动重新发送消息。
            ~~~

         2. 接收者：

            1. ~~~yaml
               spring:
                 rabbitmq:
                   host: localhost 
                   port: 5672 
                   username: study
                   password: study 
                   virtual-host: /study 
                   listener:
                     direct:
                       acknowledge-mode: manual 
               ~~~

               

            2. ~~~java
               package com.study.consumerack.listener;
               
               import com.rabbitmq.client.Channel;
               import org.springframework.amqp.core.Message;
               import org.springframework.amqp.rabbit.annotation.RabbitListener;
               import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
               import org.springframework.stereotype.Component;
               
               import java.io.IOException;
               
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
                           int i = 1/0;
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
               
               ~~~

            3. 消息发送送者使用前面创建的: springboot-producer-confret

            4. 总结:

               - 在rabbit.:listener-container标签中设置acknowledge属性，设置ack方式 none:自动确认，manual:手动确认
               - 如果在消费端没有出现异常，则调用channel.basicAck(deliveryTag,false);方法确认签收消息
               - 如果出现异常，则在catch中调用basicNack或 basicReject，拒绝消息，让MQ重新发送消息。

               

   3. 消费端限流：

      1. yaml

         ~~~yaml
         spring:
           rabbitmq:
             host: localhost  
             port: 5672 
             username: study 
             password: study 
             virtual-host: /study 
             listener:
               direct:
                 acknowledge-mode: manual 
                 prefetch: 1 # 配置属性即可，一次拉取几条
         ~~~

   4. TTL

      	![ttl](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/ttl.png)

      1. ~~~yaml
         TTL全称Time To Live (存活时间/过期时间)。
         当消息到达存活时间后，还没有被消费，会被自动清除。
         RabbitMQ可以对消息设置过期时间，也可以对整个队列(Queue）设置过期时间。
         ~~~

      2. producer：TtlSeniorConfig配置类

         1. ~~~java
            package com.study.producer.configuration;
            
            
            import org.springframework.amqp.core.*;
            import org.springframework.beans.factory.annotation.Qualifier;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            
            @Configuration
            public class TtlSeniorConfig {
                public static final String EXCHANGE_TTL_NAME= "springboot_ttl_exchange";
                public static final String QUEUE_TTL_NAME= "springboot_ttl_queue";
                //1.交换机
                @Bean("springBootTtlExchange")
                public Exchange springBootExchange(){
                    return ExchangeBuilder.topicExchange(EXCHANGE_TTL_NAME).durable(true).build();
                }
            
                //2.消息队列
                @Bean("springBooTtlQueue")
                public Queue springBootQueue(){
                    //x-message-ttl,queue过期时间
                    return QueueBuilder.durable(QUEUE_TTL_NAME).withArgument("x-message-ttl",10000).build();
                }
            
                //3.绑定交换机和消息队列
                @Bean
                public Binding bingQueueExchange(@Qualifier("springBooTtlQueue") Queue queue, @Qualifier("springBootTtlExchange") Exchange exchange){
                    return BindingBuilder.bind(queue).to(exchange).with("ttl.#").noargs();
                }
            }
            ~~~

         2. ~~~java
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
            ~~~

      3. 总结：

         ~~~
         设置队列过期时间使用参数: x-message-ttl，单位: ms(毫秒)，会对整个队列消息统一过期。
         设置消息过期时间使用参数: expiration。单位: ms(毫秒)，当该消息在队列头部时（消费时)，会单独判断这一消息是否过期。
         如果两者都进行了设置，以时间短的为准。
         ~~~

         

   5. 死信队列，可以在控制界面设置

      	![dlx](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/dlx.png)

      1. 问题出现的原因：

         ~~~java
         1.超过消息队列的长度限制
                     /**
                      * deliveryTag: 当前收到消息的一个标签
                      * multiple: 允许签收多条消息
                      * requeue: 是否将消息重返队列，true,消息重返queue,false,消息返回死信交换机
                      * */
         2.消费者拒绝签收消息， channel.basicNack(deliveryTag,true,false);
         3.原消息队列设置了消息过期时间，消息超过时间未被消费
         ~~~

         

      2. 声明配置类DlxSeniorConfig：

         ~~~java
         package com.study.producer.configuration;
         
         import org.springframework.amqp.core.*;
         import org.springframework.beans.factory.annotation.Qualifier;
         import org.springframework.context.annotation.Bean;
         import org.springframework.context.annotation.Configuration;
         
         import java.util.HashMap;
         
         @Configuration
         public class DlxSeniorConfig {
             //正常的交换机
             public static final String EXCHANGE_NORMAL_DLX_NAME = "boot_dlx_exchange";
             //死信交换机
             public static final String EXCHANGE_DLX_NAME = "dlx_exchange";
             //正常的队列
             public static final String QUEUE_NORMAL_DLX_NAME = "boot_dlx_queue";
             //死信队列
             public static final String QUEUE_DLX_NAME = "dlx_queue";
         
             //1.正常的交换机
             @Bean("bootDlxExchange")
             public Exchange bootDlxExchange(){
                 return ExchangeBuilder.topicExchange(EXCHANGE_NORMAL_DLX_NAME).durable(true).build();
             }
             //2.死信交换机
             @Bean("dlxExchange")
             public Exchange dlxExchange(){
                 return ExchangeBuilder.topicExchange(EXCHANGE_DLX_NAME).durable(true).build();
             }
         
             //3.正常的队列绑定死信交换机
             @Bean("bootDlxQueue")
             public Queue bootDlxQueue(){
                 HashMap<String, Object> dlxMap = new HashMap<>(16);
                 //设置消息过期时间：x-message-ttl
                 dlxMap.put("x-message-ttl",10000);
                 //设置最大的消息队列数：x-max-length
                 dlxMap.put("x-max-length",10);
                 //设置死信交换机
                 dlxMap.put("x-dead-letter-exchange",DlxSeniorConfig.EXCHANGE_DLX_NAME);
                 //设置信息队列的routingKey
                 dlxMap.put("x-dead-letter-routing-key","dlx.haha");
                 //正常队列绑定死信交换机
                 return QueueBuilder.durable(QUEUE_NORMAL_DLX_NAME).withArguments(dlxMap).build();
             }
             //4.死信队列
             @Bean("dlxQueue")
             public  Queue dlxQueue(){
                 return QueueBuilder.durable(QUEUE_DLX_NAME).build();
             }
         
             //5.正常队列绑定正常交换机
             @Bean
             public Binding bindBootDlxQueueExchange(@Qualifier("bootDlxQueue") Queue queue, @Qualifier("bootDlxExchange") Exchange exchange){
                 return BindingBuilder.bind(queue).to(exchange).with("boot.dlx.#").noargs();
             }
         
             //6.死信队列绑定死信交换机
             @Bean
             public Binding bindDlxQueueExchange(@Qualifier("dlxQueue") Queue queue, @Qualifier("dlxExchange") Exchange exchange){
                 return BindingBuilder.bind(queue).to(exchange).with("dlx.#").noargs();
             }
         }
         ~~~

         

      3. 声明消息生产者:测试类里编写

         ~~~
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
         ~~~

         

      4. 声明消费者监听类：在springboot-consumer-ack项目下编写DlxListener监听类

         ~~~java
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
         ~~~

      5. 总结

         - 死信交换机和死信队列和普通交换机、队列没区别
         - 消息成为死信消息后，如果绑定了死信交换机，消息会被死信交换机路由到死信队列中，没有绑定直接销毁
         - 消息成为死信消息一般有三种情况
           1. 超过消息队列的长度限制
           2. 消费者拒绝签收消息， channel.basicNack(deliveryTag,true,false);
           3. 原消息队列设置了消息过期时间，消息超过时间未被消费

   6. **延迟队列**：消息进入队列后不会立马被消费，只有达到指定的的时间后，才会被消费（RabbitMQ中并没有提供相关功能）

      1. 定时器

      2. 延迟队列

         ![delay](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/delay.png)

      3. TTL+DLX

         ![ttl_dlx](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/ttl_dlx.png)

      4. 生产者:springboot-producer-delay

         ~~~yaml
         spring:
           rabbitmq:
             host: localhost
             port: 5672
             username: study
             password: study
             virtual-host: /study
             listener:
               direct:
                 acknowledge-mode: manual
         ~~~

         ~~~java
         package com.study.producer.configuration;
         
         import org.springframework.amqp.core.*;
         import org.springframework.beans.factory.annotation.Qualifier;
         import org.springframework.context.annotation.Bean;
         import org.springframework.context.annotation.Configuration;
         
         import java.util.HashMap;
         
         //配置类
         @Configuration
         public class DelaySeniorDelay {
             /**
              * 高级特牲（延迟队列）-配置类ttl+dlx
              * 1．声明正常的队列(boot_delay_queue)和交换机(boot_delay_exchange)
              * 2．声明死信队列(delay_queue)和死信交换机(delay_exchange)
              * 3.正常风列绑定死信交换机
              *      设置两个参数:
              *          x-dead-letter-exchange:死信交换机名称
              *          x- dead-letter-routing-key:发送给死信交换机的routing key
              *          x-message-ttl:设置延迟时间
              * **/
             //正常的交换机
             public static final String EXCHANGE_NORMAL_DELAY_NAME = "boot_delay_exchange";
             //延迟交换机
             public static final String EXCHANGE_DELAY_NAME = "delay_exchange";
             //正常的队列
             public static final String QUEUE_NORMAL_DELAY_NAME = "boot_delay_queue";
             //延迟队列
             public static final String QUEUE_DELAY_NAME = "delay_queue";
         
             //1.正常的交换机
             @Bean("bootDelayExchange")
             public Exchange bootDelayExchange(){
                 return ExchangeBuilder.topicExchange(EXCHANGE_NORMAL_DELAY_NAME).durable(true).build();
             }
             //2.延迟交换机
             @Bean("delayExchange")
             public Exchange dlxDelayExchange(){
                 return ExchangeBuilder.topicExchange(EXCHANGE_DELAY_NAME).durable(true).build();
             }
         
             //3.正常的队列绑定死信交换机
             @Bean("bootDelayQueue")
             public Queue bootDelayQueue(){
                 HashMap<String, Object> dlxMap = new HashMap<>(16);
                 //设置消息过期时间：x-message-ttl
                 dlxMap.put("x-message-ttl",10000);
                 //设置最大的消息队列数：x-max-length
         //        dlxMap.put("x-max-length",10);
                 //设置死信交换机
                 dlxMap.put("x-dead-letter-exchange",DelaySeniorDelay.EXCHANGE_DELAY_NAME);
                 //设置信息队列的routingKey
                 dlxMap.put("x-dead-letter-routing-key","delay.haha");
                 //正常队列绑定死信交换机
                 return QueueBuilder.durable(QUEUE_NORMAL_DELAY_NAME).withArguments(dlxMap).build();
             }
             //4.死信队列
             @Bean("delayQueue")
             public  Queue delayQueue(){
                 return QueueBuilder.durable(QUEUE_DELAY_NAME).build();
             }
         
             //5.正常队列绑定正常交换机
             @Bean
             public Binding bindBootDlxQueueExchange(@Qualifier("bootDelayQueue") Queue queue, @Qualifier("bootDelayExchange") Exchange exchange){
                 return BindingBuilder.bind(queue).to(exchange).with("boot.delay.#").noargs();
             }
         
             //6.死信队列绑定死信交换机
             @Bean
             public Binding bindDlxQueueExchange(@Qualifier("delayQueue") Queue queue, @Qualifier("delayExchange") Exchange exchange){
                 return BindingBuilder.bind(queue).to(exchange).with("delay.#").noargs();
             }
         }
         
         ~~~

         

      5. 发送消息

         ~~~java
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
         ~~~

         

      6. 消息监听:springboot-consumer-ack:DelayListener

         ~~~java
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
         
         ~~~

      7. 总结：

         - 延迟队列指消息进入队列后，可以被延迟一定时间，再进行消费。
         - RabbitMQ没有提供延迟队列功能，但是可以使用:TTL+DLX来实现延迟队列效果。

   7. 日志与监控

   8. 消息追踪：

      1. Firehose

         ~~~
         firehose的机制是将生产者投递给rabbitmq的消息，rabbitmq投递给消费者的消息按照指定的格式发送到默认的exchange上。
         这个默认的exchange的名称为amq.rabbitmq trace，它是一个topic类型的exchange。
         发送到这个exchange上的消息的routing key为publish exchangename和deliver.queuename。
         其中exchangename和queuename为实际exchange和queue的名称，分别对应生产者投递到exchange的消息，和消费者从queue上获取的消息。
         
         注意:打开trace会影响消息写入功能，适当打开后请关闭。
         
         1.sudo rabbitmqctl trace_on:开启Firehose命令
         2.sudo rabbitmqctl trace_off:关闭Firehose命令
         ~~~

         

      2. rabbitmq_tracing

         ~~~
         rabbitmq_racing和Firehose在实现上如出一辙，只不过rabbitmq_tracing的方式比Firehose多了一层GUI的包装，更容易使用和管理。
         启用插件: sudo rabbitmq-plugins enable rabbitmq_tracing
         ~~~

         

10. **RabbitMQ的应用问题**

    1. 消息的补偿

       ![10.1](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/10.1.png)

    2. 幂等性保障

       1. ~~~
          幂等性：指一次和多次请求某一个资源，对于资源本身应该具有同样的结果。也就是说，其任意多次执行对资源本身所产生的影响均与—次执行的影响相同。
          在MQ中指，消费多条相同的消息，得到与消费该消息━次相同的结果。
          ~~~

          ![10.2.1](https://github.com/YyangZhiHeng/RabbitMQ/blob/main/picture/10.2.1.png)

11. **RabbitMQ的集群搭建**

    1. 镜像队列
    2. haproxy

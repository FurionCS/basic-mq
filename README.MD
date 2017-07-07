## Basic-mq 
### 说明：
本项目是对spring-amqp 进行的封装，可以直接打包成jar，加入到自己项目中，然后在自己项目中配置响应依赖就可以。


### 项目结构



    --org.cs.basic.mq
		--consumer
		--global
		--producer
		--rpc
		--topic
		--util    --工具类 连接工具类，对象和byte转换工具类
	--test
		--org.cs.basic.test
		--resources
		



### 使用方法
1：编译项目打包成jar,加入到自己项目中
2：在项目中加入依赖


      <dependency>  
        <groupId>com.rabbitmq</groupId>  
        <artifactId>amqp-client</artifactId>  
        <version>3.2.4</version>  
    </dependency>  
    <dependency>  
        <groupId>org.springframework.amqp</groupId>  
        <artifactId>spring-amqp</artifactId>  
        <version>1.3.4.RELEASE</version>  
    </dependency>  
    <dependency>  
        <groupId>org.springframework.amqp</groupId>  
        <artifactId>spring-rabbit</artifactId>  
        <version>1.3.4.RELEASE</version>  
    </dependency>  
    <dependency>  
        <groupId>com.caucho</groupId>  
        <artifactId>hessian</artifactId>  
        <version>4.0.7</version>  
    </dependency>  



3：获得连接

     EventControlConfig config2 = new EventControlConfig("192.168.0.19","admin","admin",1,1);

basic-mq使用手册
四种情况
	1：最普通的生产消费     Q1---Q1
	2:带路由得普通生产消费  Q1，R1---Q1，R1
	3：延迟消费                     Q1，R1，Q2，E2---Q2，E2，R1
	4:rpc生产消费            	 Q1---Q1
	
使用方法：
	1： 
	生产
	eventTemplate.send("QUEUE_TEST4", defaultExchange,"hello world");
	消费
	controller.add("QUEUE_TEST4", defaultExchange,new ApiProcessEventProcessor2());
	2：
	生产
	 eventTemplate.send("QUEUE_TEST4", defaultExchange,"routingKey","hello world");
	消费
	 controller.add("QUEUE_TEST4", defaultExchange,"routingKey",new ApiProcessEventProcessor2());  
	3:
	生产
	eventTemplate.send("Q_TIME_TEST", defaultExchange,consumerQueueName,consumerExchange,routingKey,"hello world");
	消费
	controller.add(consumerQueueName, consumerExchange,"routingKey",new ApiProcessEventProcessor2());
	还要在web页面配置下
	参考
	http://wenku.baidu.com/link?url=ZoYMqw7pxeyefWJXu6qKKRMMsmrZYKbxLqnp-HnX0bVELVvyhukDri-NAbkuzTBmDCxTWRNgvhPBT4Jix2KN7vlgg9KQ2uUjDtDuZlY7Svq
	4：
	生产
	 Object obj=eventTemplate.sendAndReceive("QUEUE_TEST1", defaultExchange, "hello world"); 
	消费  
	controller.add("QUEUE_TEST1", defaultExchange, new ApiProcessEventProcessorRPC());  
	5：
	生产
	
	 

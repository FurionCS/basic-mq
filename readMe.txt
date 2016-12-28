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
	 
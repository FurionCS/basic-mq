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



3：初始化配置

     EventControlConfig config2 = new EventControlConfig("192.168.0.19","admin","admin",1,1);

4：初始化链接

    DefaultEventController controller = DefaultEventController.getInstance(config2);  
    
    
5:发送消息
需要先获得发送模板对象

    EventTemplate eventTemplate=controller.getEopEventTemplate();
    eventTemplate.send("Q_T_Message_Expiration", defaultExchange, "hello world！", 8000, 0);

    
6:消费消息
在消费端同样要执行第一步和第二步
还需要添加监听队列，启动监听

      controller.add("Q_T_Message_Expiration", defaultExchange,new MessagePriority());      
      controller.start();



     class MessagePriority implements EventProcesser{
		public void process(Object e) {
			try {
				Thread.sleep(1000);
				System.out.println(e);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}
		
    } 


### 配置
### config配置
- public EventControlConfig(String serverHost)
-  public EventControlConfig(String serverHost,String username,String password)
-   public EventControlConfig(String serverHost,int port,String username,String password)
-  public EventControlConfig(String serverHost,String username,String password,int processThreadNum,int prefetchSize)
-   public EventControlConfig(String serverHost,String username,String password,int prefetchSize)

> 当然自己可以在EventControlConfig.java中配置


####  发送模式

- void send(String queueName,String exchangeName,Object eventContent)
-  Object sendAndReceive(String queueName,String exchangeName,Object eventContent)
-  void send(String queueName,String exchangeName,String routing,Object eventContent)
-  void send(String queueName,String exchangeName,String consumerQueueName,String consumerExchange,String routing,Object eventContent)
-   void send(String queueName,String exchangeName,String consumerQueueName,String consumerExchange,String routing,Object eventContent,int expiration,int priority)
-   void send(String queueName, String exchangeName, Object eventContent,int expiration,int priority) 
-  void send(String queueName, String exchangeName, String routing,Object eventContent,int expiration,int priority) 




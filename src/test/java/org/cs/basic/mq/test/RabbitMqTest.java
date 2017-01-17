package org.cs.basic.mq.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cs.basic.mq.consumer.EventProcesser;
import org.cs.basic.mq.consumer.EventProcesserRPC;
import org.cs.basic.mq.global.DefaultEventController;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.producer.EventTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.ReceiveAndReplyCallback;

public class RabbitMqTest {
	private String defaultHost = "192.168.0.19";  
    
    private String defaultExchange = "EXCHANGE_DIRECT_Test";  
      
    private String defaultQueue = "QUEUE_TEST1";  
      
    private DefaultEventController controller;  
      
    private EventTemplate eventTemplate;  
      
    @Before  
    public void init() throws IOException{  
   //   EventControlConfig config = new EventControlConfig(defaultHost);
//      EventControlConfig config2 = new EventControlConfig("192.168.0.19","admin","admin");
        EventControlConfig config2 = new EventControlConfig("123.207.15.66:5672,115.28.41.97:5672,115.28.180.130:5672","cs","123456");
        controller = DefaultEventController.getInstance(config2);  
        eventTemplate = controller.getEopEventTemplate();  
      //  controller.add("QUEUE_TEST2", "EXCHANGE_TIME_YOTIME","Q_TIME_TEST",new ApiProcessEventProcessor2());  
//        controller.add("QUEUE_TEST1", defaultExchange, new ApiProcessEventProcessorRPC());  
//        controller.add("QUEUE_TEST4", defaultExchange,"routingKey",new ApiProcessEventProcessor2());  
//        controller.add("QUEUE_TEST5", defaultExchange,new ApiProcessEventProcessor2());  
//        controller.add("Q_TEST", defaultExchange,new ApiProcessEventProcessor2());
//        controller.start();  
    }  
      
    @Test  
    public void sendString() throws Exception{  
    //   System.out.println("test异步");
//        Object obj=eventTemplate.sendAndReceive("QUEUE_TEST1", defaultExchange, "hello world");  
    //	 eventTemplate.send("Q_TIME_TEST", defaultExchange,"QUEUE_TEST2","EXCHANGE_TIME_YOTIME","Q_TIME_TEST","hello world");
    //	 eventTemplate.send("QUEUE_TEST4", defaultExchange,"routingKey","hello world");
    	 eventTemplate.send("QUEUE_TEST5", defaultExchange, "hello world",8000);
    	int i=0;
//    	while(true){
//    		i++;
//  		eventTemplate.send("Q_TEST", defaultExchange, i+":hello world");
    	//	Thread.sleep(1000);
//    	}
    //   System.out.println("test异步");
//      System.out.println("返回"+obj);
    }  
      
    @Test  
    public void sendObject() throws Exception{  
       // eventTemplate.send(defaultQueue, defaultExchange, mockObj());  
    }  
      
  /*  @Test  
    public void sendTemp() throws Exception, InterruptedException{  
        String tempExchange = "EXCHANGE_DIRECT_TEST_TEMP";//以前未声明的exchange  
        String tempQueue = "QUEUE_TEST_TEMP";//以前未声明的queue  
        eventTemplate.send(tempQueue, tempExchange, mockObj());  
        //发送成功后此时不会接受到消息，还需要绑定对应的消费程序  
        controller.add(tempQueue, tempExchange, new ApiProcessEventProcessor());  
    }  */
      
    @After  
    public void end() throws InterruptedException{  
        Thread.sleep(2000);  
    }  
      
    private People mockObj(){  
        People jack = new People();  
        jack.setId(1);  
        jack.setName("JACK");  
        jack.setMale(true);  
          
        List<People> friends = new ArrayList<People>();  
        friends.add(jack);  
        People hanMeiMei = new People();  
        hanMeiMei.setId(1);  
        hanMeiMei.setName("韩梅梅");  
        hanMeiMei.setMale(false);  
        hanMeiMei.setFriends(friends);  
          
        People liLei = new People();  
        liLei.setId(2);  
        liLei.setName("李雷");  
        liLei.setMale(true);  
        liLei.setFriends(friends);  
        liLei.setSpouse(hanMeiMei);  
        hanMeiMei.setSpouse(liLei);  
        return hanMeiMei;  
    }  
      
    class ApiProcessEventProcessorRPC implements EventProcesserRPC{
		public Object process(Object e) {
				System.out.println("我进自定义rpc方法咯");
				 People cs = new People();  
				 cs.setName("cs");
				return  mockObj();
		}
    } 
    class ApiProcessEventProcessor2 implements EventProcesser{
  		public void process(Object e) {
 				try {
 					Thread.sleep(100);
  					System.out.println("我进自定义方法咯:"+e);
				} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
					e1.printStackTrace();
				}
  		}
      }  
}

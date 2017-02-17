package org.cs.basic.mq.test;

import java.io.IOException;

import org.cs.basic.mq.consumer.EventProcesser;
import org.cs.basic.mq.consumer.EventProcesserRPC;
import org.cs.basic.mq.global.DefaultEventController;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.producer.EventTemplate;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Description: 对消息进行设置测试
 * @type  Test
 * @author Mr.Cheng
 * @date 2017年2月17日
 * @time 上午10:26:48
 *
 */
public class RabbitMqMessageTest {
    private String defaultExchange = "EXCHANGE_DIRECT_YOTIME";  
    private String timeExchange="EXCHANGE_TIME_YOTIME";
    private DefaultEventController controller;  
    private EventTemplate eventTemplate;  
    
    
    
    @Before  
    public void init() throws IOException{  
    	//如果要队列一个一个出必须设置为1，1
    	 EventControlConfig config2 = new EventControlConfig("192.168.0.19","admin","admin",1,1);
         controller = DefaultEventController.getInstance(config2);  
         eventTemplate = controller.getEopEventTemplate();  
//         controller.add("Q_T_Message_Expiration", defaultExchange,new MessagePriority());
//         controller.start();
    }

    @Test
    public void testPrintln(){
    	while(true){
    		
    	}
    }
    /**
     * 测试消息过期时间
     * @throws Exception 
     */
    @Test
    public void testExpiration() throws Exception{
    	eventTemplate.send("Q_T_Message_Expiration", defaultExchange, "hello world！", 8000, 0);
    }
    
    @Test
    public void testPriority() throws Exception{
    	for(int i=10;i>0;i--){
    		eventTemplate.send("Q_T_Message_Expiration", defaultExchange, "我的优先级为："+i,0,i);
    	}
    }
    
    @Test
    public void testDelay() throws Exception{
    	eventTemplate.send("Q_T_Message_Delay", defaultExchange, "Q_T_Message_Delay_C", timeExchange, "Q_T_Message_Delay", "你好",8000,0);
    }
    /**
     * 
     * Description:消息优先级测试
     * @type 
     * @author Mr.Cheng
     * @date 2017年2月17日
     * @time 上午10:50:05
     *
     */
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
    
	public RabbitMqMessageTest() {
		// TODO Auto-generated constructor stub
	}
	

}

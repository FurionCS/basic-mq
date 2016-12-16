package org.cs.basic.mq.test;

import java.io.IOException;

import org.cs.basic.mq.consumer.EventProcesserTopic;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.topic.TopicConsumer;
import org.junit.Assert;
import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

public class TopicServerTest {
	
	@Test
	public void tests() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
	    EventControlConfig config= new EventControlConfig("115.28.44.238","cs","123456");
		TopicConsumer tc=new TopicConsumer(config);
		tc.receive("topic_logs", "kernal.*", new ApiProcessEventProcessor());
	}
	 class ApiProcessEventProcessor implements EventProcesserTopic{  
	        public void process(Object e) {//消费程序这里只是打印信息  
	            Assert.assertNotNull(e);  
	            System.out.println(e);  
	            People people=null;
	            if(e instanceof People){  
	                 people = (People)e; 
	                System.out.println(people.getName());  
	            }
	        }  
	    }  
}

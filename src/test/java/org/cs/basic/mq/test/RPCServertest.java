package org.cs.basic.mq.test;

import java.io.IOException;

import org.cs.basic.mq.consumer.EventProcesser;
import org.cs.basic.mq.consumer.EventProcesserRPC;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.rpc.RPCServer;
import org.junit.Assert;
import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;


public class RPCServertest {

	@Test
	public void tests() throws ShutdownSignalException, ConsumerCancelledException, IOException, InterruptedException{
		    EventControlConfig config= new EventControlConfig("115.28.44.238","cs","123456");
		    People people=new People();
		    people.setName("css");
		    people.setId(1);
		    RPCServer rpcs=new RPCServer(config);
		    people.setMale(true);
		    rpcs.recAndCallBack("rpc_queue",new ApiProcessEventProcessor());
	}
	 class ApiProcessEventProcessor implements EventProcesserRPC{  
	        public Object process(Object e) {//消费程序这里只是打印信息  
	            Assert.assertNotNull(e);  
	            System.out.println(e);  
	            People people=null;
	            if(e instanceof People){  
	                 people = (People)e; 
	                 people.setName("cs5");
//	                System.out.println(people.getSpouse());  
//	                System.out.println(people.getFriends());  
	            }
				return people;  
	        }  
	    }  
}

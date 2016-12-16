package org.cs.basic.mq.test;

import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.rpc.RPCClient;
import org.junit.Test;


public class RPCClienttest {

	@Test
	public void testc() throws Exception{
	    EventControlConfig config= new EventControlConfig("115.28.44.238","cs","123456");
	    RPCClient rpcc=new RPCClient(config);
	    People people=new People();
	    people.setName("cs");
	    people.setId(1);
	    System.out.println("结果:"+rpcc.callServer(people, "rpc_queue",3));
	    rpcc.close();
	}
}

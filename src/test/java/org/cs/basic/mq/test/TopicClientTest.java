package org.cs.basic.mq.test;

import java.io.IOException;

import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.topic.TopicProducter;
import org.junit.Test;

public class TopicClientTest {
	
	@Test
	public void testC() throws IOException{
	    EventControlConfig config= new EventControlConfig("115.28.44.238","cs","123456");
		TopicProducter tp=new TopicProducter(config);
		People people=new People();
		people.setName("cs");
		people.setId(1);
		tp.send("topic_logs", "kernal.info", people);
	}

}

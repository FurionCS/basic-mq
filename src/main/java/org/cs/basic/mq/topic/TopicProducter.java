package org.cs.basic.mq.topic;

import java.io.IOException;

import org.cs.basic.mq.global.CodecFactory;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.global.HessionCodecFactory;
import org.cs.basic.mq.util.ConnectionUtil;

import com.rabbitmq.client.Channel;

/**
 * 主题订阅生产者
 * @author Mr.Cheng
 * @since 2016/11/29 15:57
 *
 */
public class TopicProducter {
	private Channel channel;
	private CodecFactory cf;
	public TopicProducter(){};
	private  ConnectionUtil connection;
	public TopicProducter(EventControlConfig config) throws IOException{
		connection=new ConnectionUtil(config);
		channel=connection.getChannel();
		cf=new HessionCodecFactory();
	}
	/**
	 * 发送方法
	 * @param exchanges
	 * @param routingkey
	 * @param obj   对象
	 * @throws IOException
	 */
	public void send(String exchanges,String routingkey,Object obj) throws IOException{
		 // 声明转发器
		channel.exchangeDeclare(exchanges, "topic"); 
		byte[] msg=cf.serialize(obj);
		channel.basicPublish(exchanges, routingkey, null, msg);  
		channel.close();  
	    connection.close();  
	}
}

package org.cs.basic.mq.topic;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.cs.basic.mq.consumer.EventProcesserTopic;
import org.cs.basic.mq.global.CodecFactory;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.global.HessionCodecFactory;
import org.cs.basic.mq.util.ConnectionUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 主题订阅消费者
 * @author Mr.Cheng
 *
 */
public class TopicConsumer {
	private static Logger logger=Logger.getLogger(TopicConsumer.class);
	private Channel channel;
	private CodecFactory cf;
	private  ConnectionUtil connection;
	public TopicConsumer(){};
	public TopicConsumer(EventControlConfig config) throws IOException{
		connection=new ConnectionUtil(config);
		channel=connection.getChannel();
		cf=new HessionCodecFactory();
	}
	/**
	 * 消费方法
	 * @param exchagnes 转换器
	 * @param routingkey  路由
	 * @param ept  处理类
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void receive(String exchagnes,String routingkey,EventProcesserTopic ept) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		  // 声明转发器  
        channel.exchangeDeclare(exchagnes, "topic");  
        // 随机生成一个队列  
        String queueName = channel.queueDeclare().getQueue();  
        //接收所有与kernel相关的消息  
        channel.queueBind(queueName, exchagnes, routingkey); 
        logger.info("[*] Waiting for messages about topic. To exit press CTRL+C");
        QueueingConsumer consumer = new QueueingConsumer(channel);  
        channel.basicConsume(queueName, true, consumer);
        while (true) {  
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();  
            Object message = cf.deSerialize(delivery.getBody());  
            ept.process(message);
        }  
	}
}

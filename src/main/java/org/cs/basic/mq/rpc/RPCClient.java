package org.cs.basic.mq.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.cs.basic.mq.global.CodecFactory;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.global.HessionCodecFactory;
import org.cs.basic.mq.util.ConnectionUtil;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
/**
 * RPC生产者
 * @author Mr.Cheng
 * @since 2016/11/29 15:53
 */
public class RPCClient {
	
	private static Logger logger=Logger.getLogger(RPCClient.class);
	private ConnectionUtil connection;
	private Channel channel;
	private String replyQueueName;
	private QueueingConsumer consumer;
	final ExecutorService exec = Executors.newFixedThreadPool(1); 
	private CodecFactory cf;     //序列化工厂
	private String corrId="";    
	public RPCClient(){};
	public RPCClient(EventControlConfig config) throws Exception {
		//•	先建立一个连接和一个通道，并为回调声明一个唯一的'回调'队列
		connection=new ConnectionUtil(config);
		channel=connection.getChannel();
		//•	注册'回调'队列，这样就可以收到RPC响应
		replyQueueName = channel.queueDeclare().getQueue();
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
		cf=new HessionCodecFactory();
	}

	/**
	 * 发送对象到rpc服务端
	 * @param message            //发送对象
	 * @param requestQueueName  //请求队列名
	 * @param time   //超时时间 单位秒
	 * @return     
	 * @throws Exception
	 */
	public Object callServer(Object message,String requestQueueName,int time) throws Exception {
		Object response = null;
		 corrId = java.util.UUID.randomUUID().toString();
		//发送请求消息，消息使用了两个属性：replyto和correlationId
		BasicProperties props = new BasicProperties.Builder()
				.correlationId(corrId).replyTo(replyQueueName).build();
		channel.basicPublish("", requestQueueName, props, cf.serialize(message));
		//等待接收结果
		try { 
				//超时处理
				Future<Object> future = exec.submit(call);
				if(time<1) time=1;
				response = future.get(1000*time, TimeUnit.MILLISECONDS); //任务处理超时时间默认设为 1 秒  
		} catch (TimeoutException ex) {  
				response="服务端处理超时！";
				logger.error(ex.getMessage());
	    } catch (Exception e) {  
	    		response="处理失败";
	    		logger.error(e.getMessage());
	    }  
	   // 关闭线程池  
	    exec.shutdown();  
		return response;
	}
	//处理函数
	 Callable<Object> call = new Callable<Object>() {  
			public Object call() throws Exception {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				//检查它的correlationId是否是我们所要找的那个
				if (delivery.getProperties().getCorrelationId().equals(corrId)) {
					return cf.deSerialize(delivery.getBody());
				}
				return "correlationId不对";
			}  
	}; 
	/**
	 * 关闭
	 * @throws Exception
	 */
	public void close() throws Exception {
		if(connection!=null){
			connection.close();
		}
	}
}

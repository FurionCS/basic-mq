package org.cs.basic.mq.rpc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.cs.basic.mq.consumer.EventProcesserRPC;
import org.cs.basic.mq.global.CodecFactory;
import org.cs.basic.mq.global.EventControlConfig;
import org.cs.basic.mq.global.HessionCodecFactory;
import org.cs.basic.mq.util.ConnectionUtil;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
/**
 * RPC消费者
 * @author Mr.Cheng
 * @since 2016/11/29 15:54
 */
public class RPCServer {
	private static Logger logger=Logger.getLogger(RPCServer.class); 
	private Channel channel;
	private CodecFactory cf;
	private BasicProperties props;
	private BasicProperties replyProps;
	private QueueingConsumer.Delivery delivery;
	private QueueingConsumer consumer;
	private EventProcesserRPC ephandle;
	public RPCServer(){};
	public RPCServer(EventControlConfig config) throws IOException{
		ConnectionUtil connection=new ConnectionUtil(config);
		channel=connection.getChannel();
		cf=new HessionCodecFactory();
	}
	/**
	 * 接受并回调
	 * @param RPC_QUEUE_NAME
	 * @param ep
	 * @throws IOException
	 * @throws ShutdownSignalException
	 * @throws ConsumerCancelledException
	 * @throws InterruptedException
	 */
	public void recAndCallBack(String RPC_QUEUE_NAME,EventProcesserRPC ep) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		
		channel.queueDeclare(RPC_QUEUE_NAME, true, false, false, null);
		channel.basicQos(1);
		 consumer = new QueueingConsumer(channel);
		//打开应答机制autoAck=false
		channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
		logger.info("[x] Awaiting RPC requests...");
		ephandle=ep;
		Runnable main=new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				try {
					delivery = consumer.nextDelivery();
					 props = delivery.getProperties();
					 replyProps = new BasicProperties.Builder()
							.correlationId(props.getCorrelationId()).build();
					 Object message = cf.deSerialize(delivery.getBody());
					 Object remessage=ephandle.process(message);
					//返回处理结果队列
					 channel.basicPublish("", props.getReplyTo(), replyProps,
								cf.serialize(remessage));
						//发送应答 
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				System.out.println("11");
			}
			
		};
		new Thread(main).start();
	}
}

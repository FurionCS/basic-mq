package org.cs.basic.mq.producer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.cs.basic.mq.global.CodecFactory;
import org.cs.basic.mq.global.EventMessage;
import org.cs.basic.mq.util.ObjectAndByte;
import org.jboss.logging.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
/**
 * 默认消息模板
 * @author Mr.Cheng
 *
 */
public class DefaultEventTemplate implements EventTemplate {

	 private static final Logger logger = Logger.getLogger(DefaultEventTemplate.class);  
	  
	    private AmqpTemplate eventAmqpTemplate;  
	  
	    private CodecFactory defaultCodecFactory;  
	  
	//  private DefaultEventController eec;  
	//  
	//  public DefaultEventTemplate(AmqpTemplate eopAmqpTemplate,  
//	          CodecFactory defaultCodecFactory, DefaultEventController eec) {  
//	      this.eventAmqpTemplate = eopAmqpTemplate;  
//	      this.defaultCodecFactory = defaultCodecFactory;  
//	      this.eec = eec;  
	//  }  
	      
	    public DefaultEventTemplate(AmqpTemplate eopAmqpTemplate,CodecFactory defaultCodecFactory) {  
	        this.eventAmqpTemplate = eopAmqpTemplate;  
	        this.defaultCodecFactory = defaultCodecFactory;  
	    }  
	  
	    /**
	     * 普通消费模式
	     * @param queueName   队列名
	     * @param exchangeName 转换器
	     * @param eventContent 对象
	     */
	    public void send(String queueName, String exchangeName, Object eventContent)  
	            throws Exception {  
	        this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory,0,queueName,0,0);  
	    }    
	    /**
	     * 带路由的普通消费模式
	     * @param queueName   队列
	     * @param exchangeName 转换器
	     * @param routing  路由
	     * @param eventContent  对象
	     */
		public void send(String queueName, String exchangeName, String routing,
				Object eventContent) throws Exception {
			this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory, 0, routing,0,0);
		}  
		  /**
	     * 普通消费模式,对message进行设置
	     * @param queueName  队列名
	     * @param exchangeName 转化器
	     * @param eventContent 对象
	     * @param expiration 过期时间 (毫秒) 0表示不过期
	     * @param priority  优先级  0(默认，也是最低)
	     * @author Mr.Cheng
	     */
	    public void send(String queueName, String exchangeName, Object eventContent,int expiration,int priority)  
	            throws Exception {  
	        this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory,0,queueName,expiration,priority);  
	    } 
	   
	    /**
	     * 带路由的普通消费模式，对message进行设置
	     * @param queueName  队列名
	     * @param exchangeName 转换器
	     * @param routing  路由
	     * @param eventContent 对象
	     * @param expiration 过期时间 (毫秒) 0表示不过期
	     * @param priority  优先级  0(默认，也是最低)
	     * 
	     */
		public void send(String queueName, String exchangeName, String routing,
				Object eventContent,int expiration,int priority) throws Exception {
			this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory, 0, routing,expiration,priority);
		}  

		/**
		 * 队列延迟消费模式
		 */
		public void send(String queueName, String exchangeName, String consumerQueueName,String consumerExchange,String routing,
				Object eventContent) throws Exception {
			this.send(queueName, exchangeName,consumerQueueName,consumerExchange,eventContent, defaultCodecFactory, 2, routing,0,0);
		} 
		/**
		 * 队列延迟消费模式
		 * 消息过期时间
		 */
		public void send(String queueName, String exchangeName, String consumerQueueName,String consumerExchange,String routing,
				Object eventContent,int expiration,int priority) throws Exception {
			this.send(queueName, exchangeName,consumerQueueName,consumerExchange,eventContent, defaultCodecFactory, 2, routing,expiration,priority);
		} 
		/**
		 * rpc模式
		 * @param queueName  队列名
		 * @param exchangeName  转换器
		 * @param eventContent  对象
		 */
		public Object sendAndReceive(String queueName, String exchangeName,
				Object eventContent) throws Exception {
					return  this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory,1,queueName,0,0);  
		}

		
	    private Object send(String queueName, String exchangeName, String consumerQueueName,String consumerExchange, Object eventContent,  
	            CodecFactory codecFactory,int type,String routingKey,int expiration,int priority) throws Exception {  
	        if (StringUtils.isEmpty(queueName) || StringUtils.isEmpty(exchangeName) || StringUtils.isEmpty(routingKey)) {  
	            throw new Exception("queueName exchangeName routingKey can not be empty.");  
	        }  
	          
//	      if (!eec.beBinded(exchangeName, queueName))  
//	          eec.declareBinding(exchangeName, queueName);  
	  
	        byte[] eventContentBytes = null;  
	        if (codecFactory == null) {  
	            if (eventContent == null) {  
	                logger.warn("Find eventContent is null,are you sure...");  
	            } else {  
	                throw new Exception(  
	                        "codecFactory must not be null ,unless eventContent is null");  
	            }  
	        } else {  
	            try {  
	                eventContentBytes = codecFactory.serialize(eventContent);  
	            } catch (IOException e) {  
	                throw new Exception(e);  
	            }  
	        }  
	        Object obj=null;
	        // 构造成Message  
	        EventMessage msg = new EventMessage(queueName, exchangeName,routingKey, consumerQueueName,consumerExchange,
	                eventContentBytes,type); 
	        MessageProperties messageProperties=new MessageProperties();
	        if(expiration>0){ //过期时间
	        	messageProperties.setExpiration(String.valueOf(expiration));
	        }
	        if(priority>0){  //消息优先级
	        	messageProperties.setPriority(Integer.valueOf(priority));
	        }
	        Message message=new Message(ObjectAndByte.ObjectToByte(msg),messageProperties);
	        try {  
	        	if(type==0 || type==2){   //普通
	        	//	eventAmqpTemplate.convertAndSend(exchangeName, routingKey, msg);
	        		/**
	        		 * 通过send方法发送的数据为message类型，主要是为了配合messageProperties可以对消息进行控制
	        		 */
	        		eventAmqpTemplate.send(exchangeName, routingKey, message);
	        	}else if(type==1){  //rpc
	        		obj=eventAmqpTemplate.convertSendAndReceive(routingKey,msg);
//	        		obj=eventAmqpTemplate.sendAndReceive(routingKey,message);
	        	}
	        } catch (AmqpException e) {  
	            logger.error("send event fail. Event Message : [" + eventContent + "]", e);  
	            throw new Exception("send event fail", e);  
	        }
			return obj;  
	    }
}

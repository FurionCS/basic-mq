package org.cs.basic.mq.producer;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.cs.basic.mq.global.CodecFactory;
import org.cs.basic.mq.global.EventMessage;
import org.jboss.logging.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
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
	     */
	    public void send(String queueName, String exchangeName, Object eventContent)  
	            throws Exception {  
	        this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory,0,queueName);  
	    }    
	    /**
	     * 带路由的普通消费模式
	     */
		public void send(String queueName, String exchangeName, String routing,
				Object eventContent) throws Exception {
			this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory, 0, routing);
		}  
		/**
		 * 延迟消费模式
		 */
		public void send(String queueName, String exchangeName, String consumerQueueName,String consumerExchange,String routing,
				Object eventContent) throws Exception {
			this.send(queueName, exchangeName,consumerQueueName,consumerExchange,eventContent, defaultCodecFactory, 2, routing);
		} 
		/**
		 * rpc模式
		 */
		public Object sendAndReceive(String queueName, String exchangeName,
				Object eventContent) throws Exception {
					return  this.send(queueName, exchangeName, null,null,eventContent, defaultCodecFactory,1,queueName);  
		}

		
	    private Object send(String queueName, String exchangeName, String consumerQueueName,String consumerExchange, Object eventContent,  
	            CodecFactory codecFactory,int type,String routingKey) throws Exception {  
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
	        try {  
	        	if(type==0 || type==2){   //普通
	        		eventAmqpTemplate.convertAndSend(exchangeName, routingKey, msg); 
	        	}else if(type==1){  //rpc
	        		obj=eventAmqpTemplate.convertSendAndReceive(routingKey,msg);
	        	}
	        } catch (AmqpException e) {  
	            logger.error("send event fail. Event Message : [" + eventContent + "]", e);  
	            throw new Exception("send event fail", e);  
	        }
			return obj;  
	    }

	    
		



}

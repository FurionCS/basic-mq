package org.cs.basic.mq.global;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.cs.basic.mq.consumer.EventProcesser;
import org.cs.basic.mq.consumer.EventProcesserRPC;
import org.jboss.logging.Logger;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * * MessageListenerAdapter的Pojo 
 * <p>消息处理适配器，主要功能：</p> 
 * <p>1、将不同的消息类型绑定到对应的处理器并本地缓存，如将queue01+exchange01的消息统一交由A处理器来出来</p> 
 * <p>2、执行消息的消费分发，调用相应的处理器来消费属于它的消息</p> 
 * @author Mr.Cheng
 *
 */
public class MessageAdapterHandler {
	 private static final Logger logger = Logger.getLogger(MessageAdapterHandler.class);  
	  
	    private ConcurrentMap<String, EventProcessorWrap> epwMap;  
	  
	    public MessageAdapterHandler() {  
	        this.epwMap = new ConcurrentHashMap<String, EventProcessorWrap>();  
	    }  
	  
	/*    public void handleMessage(EventMessage eem) throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {  
	        logger.debug("Receive an EventMessage: [" + eem + "]");  
	        // 先要判断接收到的message是否是空的，在某些异常情况下，会产生空值  
	        if (eem == null) {  
	            logger.warn("Receive an null EventMessage, it may product some errors, and processing message is canceled.");  
	            return;  
	        }  
	        if (StringUtils.isEmpty(eem.getQueueName()) || StringUtils.isEmpty(eem.getExchangeName())) {  
	            logger.warn("The EventMessage's queueName and exchangeName is empty, this is not allowed, and processing message is canceled.");  
	            return;  
	        }  
	        // 解码，并交给对应的EventHandle执行  
	        EventProcessorWrap eepw = epwMap.get(eem.getQueueName()+"|"+eem.getExchangeName());  
	        if (eepw == null) {  
	            logger.warn("Receive an EopEventMessage, but no processor can do it.");  
	            return;  
	        }  
	        try {  
	        	System.out.println(eem.getType());
	        	if(eem.getType()==0){
	        		eepw.process(eem.getEventData());  
	        	}
	         
	        } catch (IOException e) {  
	            logger.error("Event content can not be Deserialized, check the provided CodecFactory.",e);  
	            return;  
	        }  
	    }  */
	    public Object receiveMessage(EventMessage eem) throws IOException{
	    		 if (eem == null) {  
		            logger.warn("Receive an null EventMessage, it may product some errors, and processing message is canceled.");  
		            return "error";  
		        }  
		        if (StringUtils.isEmpty(eem.getQueueName()) || StringUtils.isEmpty(eem.getExchangeName())) {  
		            logger.warn("The EventMessage's queueName and exchangeName is empty, this is not allowed, and processing message is canceled.");  
		            return "error";  
		        }  
		        System.out.println(eem.getExchangeName());
		        // 解码，并交给对应的EventHandle执行  
		        EventProcessorWrap eepw = epwMap.get(eem.getQueueName()+"|"+eem.getExchangeName());  
		        if (eepw == null) {  
		            logger.warn("Receive an EopEventMessage, but no processor can do it.");  
		            return "error";  
		        }  
		        Object obj=null;
		        if(eem.getType()==0){
		        	eepw.process(eem.getEventData());
		        }else if(eem.getType()==1){
		        	obj=eepw.processRpc(eem.getEventData());
		        }
		        return obj;
	    }
	    protected void add(String queueName, String exchangeName, EventProcesser processor,CodecFactory codecFactory) {  
	        if (StringUtils.isEmpty(queueName) || StringUtils.isEmpty(exchangeName) || processor == null || codecFactory == null) {  
	            throw new RuntimeException("queueName and exchangeName can not be empty,and processor or codecFactory can not be null. ");  
	        }  
	        EventProcessorWrap epw = new EventProcessorWrap(codecFactory,processor);  
	        EventProcessorWrap oldProcessorWrap = epwMap.putIfAbsent(queueName + "|" + exchangeName, epw);  
	        if (oldProcessorWrap != null) {  
	            logger.warn("The processor of this queue and exchange exists, and the new one can't be add");  
	        }  
	    }  
	    protected void add(String queueName, String exchangeName, EventProcesserRPC processor,CodecFactory codecFactory) {  
	        if (StringUtils.isEmpty(queueName) || StringUtils.isEmpty(exchangeName) || processor == null || codecFactory == null) {  
	            throw new RuntimeException("queueName and exchangeName can not be empty,and processor or codecFactory can not be null. ");  
	        }  
	        EventProcessorWrap epw = new EventProcessorWrap(codecFactory,processor);  
	        EventProcessorWrap oldProcessorWrap = epwMap.putIfAbsent(queueName + "|" + exchangeName, epw);  
	        if (oldProcessorWrap != null) {  
	            logger.warn("The processor of this queue and exchange exists, and the new one can't be add");  
	        }  
	    }  
	  
	    protected Set<String> getAllBinding() {  
	        Set<String> keySet = epwMap.keySet();  
	        return keySet;  
	    }  
	  
	    protected static class EventProcessorWrap {  
	    
	        private CodecFactory codecFactory;  
	  
	        private EventProcesser eep;  
	        
	        private EventProcesserRPC rpc;
	  
	        protected EventProcessorWrap(CodecFactory codecFactory,  
	                EventProcesser eep) {  
	            this.codecFactory = codecFactory;  
	            this.eep = eep;  
	        }  
	        protected EventProcessorWrap(CodecFactory codecFactory,  
	        		EventProcesserRPC rpc) {  
	            this.codecFactory = codecFactory;  
	            this.rpc=rpc;
	        }  
	  
	        public void process(byte[] eventData) throws IOException{  
	            Object obj = codecFactory.deSerialize(eventData);  
	            eep.process(obj);  
	        }  
	        public Object processRpc(byte[] eventData) throws IOException{  
	            Object obj = codecFactory.deSerialize(eventData);  
	            return rpc.process(obj);  
	        }  
	    }  
}

package org.cs.basic.mq.global;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.cs.basic.mq.consumer.EventProcesser;
import org.cs.basic.mq.consumer.EventProcesserRPC;
import org.cs.basic.mq.producer.DefaultEventTemplate;
import org.cs.basic.mq.producer.EventTemplate;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.ReceiveAndReplyCallback;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;


/**
 * 和rabbitmq通信的控制器，主要负责： 
 * <p>1、和rabbitmq建立连接</p> 
 * <p>2、声明exChange和queue以及它们的绑定关系</p> 
 * <p>3、启动消息监听容器，并将不同消息的处理者绑定到对应的exchange和queue上</p> 
 * <p>4、持有消息发送模版以及所有exchange、queue和绑定关系的本地缓存</p> 
 * @author Mr.Cheng
 *
 */
public class DefaultEventController implements EventController {

		private CachingConnectionFactory rabbitConnectionFactory;  
     
	    private EventControlConfig config;  
	      
	    private RabbitAdmin rabbitAdmin;  
	    
	    private static RabbitTemplate rabbitTemplate;
	      
	    private CodecFactory defaultCodecFactory = new HessionCodecFactory();  
	      
	    private SimpleMessageListenerContainer msgListenerContainer; // rabbitMQ msg listener container  
	      
	    private MessageAdapterHandler msgAdapterHandler = new MessageAdapterHandler();  
	      
	    private MessageConverter serializerMessageConverter = new SerializerMessageConverter(); // 直接指定  
	    //queue cache, key is exchangeName  
	    private Map<String, DirectExchange> exchanges = new HashMap<String,DirectExchange>();  
	    //queue cache, key is queueName  
	    private Map<String, Queue> queues = new HashMap<String, Queue>();  
	    //bind relation of queue to exchange cache, value is exchangeName | queueName  
	    private Set<String> binded = new HashSet<String>();  
	      
	    private EventTemplate eventTemplate; // 给App使用的Event发送客户端  
	      
	    private AtomicBoolean isStarted = new AtomicBoolean(false);  
	      
	    private static DefaultEventController defaultEventController;  
	      
	    public synchronized static DefaultEventController getInstance(EventControlConfig config){  
	        if(defaultEventController==null){  
	            defaultEventController = new DefaultEventController(config);  
	        }  
	        return defaultEventController;  
	    }  
	      
	    private DefaultEventController(EventControlConfig config){  
	        if (config == null) {  
	            throw new IllegalArgumentException("Config can not be null.");  
	        }  
	        this.config = config;  
	        initRabbitConnectionFactory();  
	        // 初始化AmqpAdmin  
	        rabbitAdmin = new RabbitAdmin(rabbitConnectionFactory);  
	        // 初始化RabbitTemplate  
	        rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);  
	        rabbitTemplate.setMessageConverter(serializerMessageConverter);  
	        eventTemplate = new DefaultEventTemplate(rabbitTemplate,defaultCodecFactory);  
	    }  
	    
	    public static RabbitTemplate getRabbitTemplate(){
	    	return rabbitTemplate;
	    }
	      
	    /** 
	     * 初始化rabbitmq连接 
	     */  
	    private void initRabbitConnectionFactory() {  
	        rabbitConnectionFactory = new CachingConnectionFactory();  
	        if(config.getServerHost().contains(",")){
	        	rabbitConnectionFactory.setAddresses(config.getServerHost());
	        }else{
	        	rabbitConnectionFactory.setHost(config.getServerHost());  
	        	 rabbitConnectionFactory.setPort(config.getPort()); 
	        }
	        rabbitConnectionFactory.setChannelCacheSize(config.getEventMsgProcessNum());  
	        
	        rabbitConnectionFactory.setUsername(config.getUsername());  
	        rabbitConnectionFactory.setPassword(config.getPassword());  
	        if (!StringUtils.isEmpty(config.getVirtualHost())) {  
	            rabbitConnectionFactory.setVirtualHost(config.getVirtualHost());  
	        }  
	    }  
	      
	    public CachingConnectionFactory getRabbitConnectionFactory() {
			return rabbitConnectionFactory;
		}

		/** 
	     * 注销程序 
	     */  
	    public synchronized void destroy() throws Exception {  
	        if (!isStarted.get()) {  
	            return;  
	        }  
	        msgListenerContainer.stop();  
	        eventTemplate = null;  
	        rabbitAdmin = null;  
	        rabbitConnectionFactory.destroy();  
	    }  
	      
	    public void start() {  
	        if (isStarted.get()) {  
	            return;  
	        }  
	        Set<String> mapping = msgAdapterHandler.getAllBinding();  
	        for (String relation : mapping) {  
	            String[] relaArr = relation.split("\\|");  
	            declareBinding(relaArr[1], relaArr[0],relaArr[2]);  
	        }  
	        initMsgListenerAdapter();  
	        isStarted.set(true);  
	    }  
	      
	    /** 
	     * 初始化消息监听器容器 
	     */  
	    private void initMsgListenerAdapter(){  
	    	MessageListener listener =new MessageListenerAdapter(msgAdapterHandler,"receiveMessage");
	        //MessageListener listener = new MessageListenerAdapter(msgAdapterHandler,serializerMessageConverter);  
	        msgListenerContainer = new SimpleMessageListenerContainer();  
	        msgListenerContainer.setConnectionFactory(rabbitConnectionFactory);  
	        msgListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);  
	        msgListenerContainer.setMessageListener(listener);  
	        msgListenerContainer.setErrorHandler(new MessageErrorHandler());  
	        msgListenerContainer.setPrefetchCount(config.getPrefetchSize()); // 设置每个消费者消息的预取值  
	        msgListenerContainer.setConcurrentConsumers(config.getEventMsgProcessNum());  //消费者数量 
	        msgListenerContainer.setTxSize(config.getPrefetchSize());//设置有事务时处理的消息数  
	        msgListenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()])); 
	        msgListenerContainer.start();  
	    }  
	  
	
	    public EventTemplate getEopEventTemplate() {  
	        return eventTemplate;  
	    }  
	  
	      
	    public EventController add(String queueName, String exchangeName,EventProcesser eventProcesser) {  
	        return add(queueName, exchangeName, eventProcesser, defaultCodecFactory,queueName);  
	    }  
	    public EventController add(String queueName, String exchangeName,
				String routingKey, EventProcesser eventProcesser) {
			return add(queueName, exchangeName, eventProcesser, defaultCodecFactory,routingKey);
		}  
	    public EventController add(String queueName, String exchangeName,EventProcesser eventProcesser,CodecFactory codecFactory,String routingKey) {  
	        msgAdapterHandler.add(queueName, exchangeName, eventProcesser, defaultCodecFactory,routingKey);  
	        if(isStarted.get()){  
	            initMsgListenerAdapter();  
	        }  
	        return this;  
	    }  
		
	    public EventController add(String queueName, String exchangeName,EventProcesserRPC eventProcesser) {  
	        return add(queueName, exchangeName, eventProcesser, defaultCodecFactory,queueName);  
	    }  
	    public EventController add(String queueName, String exchangeName,
				String routingKey, EventProcesserRPC eventProcesser) {
			return add(queueName, exchangeName, eventProcesser, defaultCodecFactory,routingKey);
		}
	    public EventController add(String queueName, String exchangeName,EventProcesserRPC eventProcesser,CodecFactory codecFactory,String routingKey) {  
	        msgAdapterHandler.add(queueName, exchangeName, eventProcesser, defaultCodecFactory,routingKey);  
	        if(isStarted.get()){  
	            initMsgListenerAdapter();  
	        }  
	        return this;  
	    }  	   
	    public EventController add(Map<String, String> bindings,  
	            EventProcesser eventProcesser) {  
	        return add(bindings, eventProcesser,defaultCodecFactory);  
	    }  
	  
	    public EventController add(Map<String, String> bindings,  
	            EventProcesser eventProcesser, CodecFactory codecFactory) {  
	        for(Map.Entry<String, String> item: bindings.entrySet())   
	            msgAdapterHandler.add(item.getKey(),item.getValue(), eventProcesser,codecFactory,item.getKey());  
	        return this;  
	    }  
	      
	    /** 
	     * exchange和queue是否已经绑定 
	     */  
	    protected boolean beBinded(String exchangeName, String queueName,String routingKey) {  
	        return binded.contains(exchangeName+"|"+queueName+"|"+routingKey);  
	    }  
	      
	    /** 
	     * 声明exchange和queue已经它们的绑定关系 
	     */  
	    protected synchronized void declareBinding(String exchangeName, String queueName,String routingKey) {  
	        String bindRelation = exchangeName+"|"+queueName+"|"+routingKey;  
	        if (binded.contains(bindRelation)) return;  
	          
	        boolean needBinding = false;  
	        DirectExchange directExchange = exchanges.get(exchangeName);  
	        if(directExchange == null) {  
	        	/**
	        	 * 声明direct模式的exchange
	        	 * 如果要声明topic的 new TopicExchange(exchangeName, true, false, null);
	        	 * 广播fanout:  new FanoutExchange(name, durable, autoDelete)
	        	 * 这里可以修改成更灵活的，但是也可以通过web中配置exchange的模式
	        	 */
	            directExchange = new DirectExchange(exchangeName, true, false, null);
  
	            exchanges.put(exchangeName, directExchange);  
	            rabbitAdmin.declareExchange(directExchange);//声明exchange  
	            needBinding = true;  
	        }  
	          
	        Queue queue = queues.get(queueName);  
	        if(queue == null) {  
	        	/**
	        	 * Queue(name, durable, exclusive,  autoDelete)
	        	 * durable是否持久化，exclusive：是否排外，autoDelete：是否自动删除
	        	 * 持久的意思是：是否保存到erlang自带得数据库mnesia中，即重启服务是否消失
	        	 * 排外的意思是：当前定义的队列是connection中的channel共享的，其他connection连接访问不到
	        	 * 当connection.close时队列删除
	        	 * 
	        	 *  new Queue(name, durable, exclusive, autoDelete, arguments)
	        	 *  arguments是队列得一些特性
	        	 *  map.put("x-message-ttl",1000*8)
	        	 */
	            queue = new Queue(queueName, true, false, false);
	            queues.put(queueName, queue);  
	            rabbitAdmin.declareQueue(queue);    //声明queue  
	            needBinding = true;  
	        }  
	          
	        if(needBinding) {  
	        	/**
	        	 * 绑定队列到exchange上，并且设置routingkey
	        	 * to（directExchange），to(topicExchange),to(fanoutExchange)..
	        	 */
	            Binding binding = BindingBuilder.bind(queue).to(directExchange).with(routingKey);
	            rabbitAdmin.declareBinding(binding);//声明绑定关系  
	            binded.add(bindRelation);  
	        }  
	    }

}

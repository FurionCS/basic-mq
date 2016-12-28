package org.cs.basic.mq.global;

import java.io.Serializable;
import java.util.Arrays;
/**
 * web和rabbitmq之间传递消息的持有对象
 * @author Mr.Cheng
 *
 */
public class EventMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String queueName;  
    
    private String exchangeName;  
    
    private String routingKey;
    
    private String consumerQueueName;
    
    private String consumerExchange;
      
    private byte[] eventData;  
    
    private int type;
  
    public EventMessage(String queueName, String exchangeName, byte[] eventData) {  
        this.queueName = queueName;  
        this.exchangeName = exchangeName;  
        this.eventData = eventData;
        this.type=0;
    }  
    
  
    public EventMessage(String queueName, String exchangeName,
			byte[] eventData, int type) {
		super();
		this.queueName = queueName;
		this.exchangeName = exchangeName;
		this.eventData = eventData;
		this.type = type;
	}
    


	public EventMessage(String queueName, String exchangeName,
			String routingKey, byte[] eventData, int type) {
		super();
		this.queueName = queueName;
		this.exchangeName = exchangeName;
		this.routingKey = routingKey;
		this.eventData = eventData;
		this.type = type;
	}
	

	public EventMessage(String queueName, String exchangeName,
			String routingKey, String consumerQueueName,
			String consumerExchange, byte[] eventData, int type) {
		super();
		this.queueName = queueName;
		this.exchangeName = exchangeName;
		this.routingKey = routingKey;
		this.consumerQueueName = consumerQueueName;
		this.consumerExchange = consumerExchange;
		this.eventData = eventData;
		this.type = type;
	}


	public String getConsumerExchange() {
		return consumerExchange;
	}


	public EventMessage() {  
    }     
  
    public String getQueueName() {  
        return queueName;  
    }  
  
    public String getExchangeName() {  
        return exchangeName;  
    }  
  
    public byte[] getEventData() {  
        return eventData;  
    }  
    public int getType() {
		return type;
	}
	public String getRoutingKey() {
		return routingKey;
	}
	public String getConsumerQueueName() {
		return consumerQueueName;
	}


	@Override
	public String toString() {
		return "EventMessage [queueName=" + queueName + ", exchangeName="
				+ exchangeName + ", routingKey=" + routingKey + ", eventData="
				+ Arrays.toString(eventData) + ", type=" + type + "]";
	}

	
	
}

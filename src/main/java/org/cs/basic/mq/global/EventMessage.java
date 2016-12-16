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




	@Override  
    public String toString() {  
        return "EopEventMessage [queueName=" + queueName + ", exchangeName="  
                + exchangeName + ", eventData=" + Arrays.toString(eventData)  
                + "]";  
    }  
}

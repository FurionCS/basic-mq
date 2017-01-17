package org.cs.basic.mq.producer;


/**
 * 发送功能接口
 * @author Mr.Cheng
 *
 */
public interface EventTemplate {
	/**
	 * 普通
	 * @param queueName
	 * @param exchangeName
	 * @param eventContent
	 * @throws Exception
	 */
    void send(String queueName,String exchangeName,Object eventContent) throws Exception;  
    /**
     * rpc
     * @param queueName
     * @param exchangeName
     * @param eventContent
     * @return
     * @throws Exception
     */
    Object sendAndReceive(String queueName,String exchangeName,Object eventContent) throws Exception;
    
    /**
     * 带路由的普通消费
     * @param queueName
     * @param exchangeName
     * @param routing
     * @param eventContent
     * @throws Exception
     */
    void send(String queueName,String exchangeName,String routing,Object eventContent) throws Exception;
    
    /**
     * 延迟消费
     * @param queueName
     * @param exchangeName
     * @param consumerQueueName
     * @param consumerExchange
     * @param routing
     * @param eventContent
     * @throws Exception
     */
    void send(String queueName,String exchangeName,String consumerQueueName,String consumerExchange,String routing,Object eventContent) throws Exception;
    /**
     * 普通消息，对消息进行过期设置
     * @param queueName
     * @param exchangeName
     * @param eventContent
     * @param expiration  过期时间
     * @param priority 优先级
     * @throws Exception
     */
     void send(String queueName, String exchangeName, Object eventContent,int expiration,int priority) throws Exception;
     /**
      * 路由消息，对消息进行过期设置
      * @param queueName
      * @param exchangeName
      * @param routing
      * @param eventContent
      * @param expiration 过期时间
      * @param priority  优先级
      * @throws Exception
      */
      void send(String queueName, String exchangeName, String routing,Object eventContent,int expiration,int priority) throws Exception;
}

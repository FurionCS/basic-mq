package org.cs.basic.mq.producer;


/**
 * 发送功能接口
 * @author Mr.Cheng
 *
 */
public interface EventTemplate {
    void send(String queueName,String exchangeName,Object eventContent) throws Exception;  
    Object sendAndReceive(String queueName,String exchangeName,Object eventContent) throws Exception;
}

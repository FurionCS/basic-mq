package org.cs.basic.mq.consumer;
/**
 * 消费接口(所有消费者方法都实现)
 * @author Mr.Cheng
 *
 */
public interface EventProcesser {
	 public void process(Object e);  
}

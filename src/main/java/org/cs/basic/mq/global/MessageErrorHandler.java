package org.cs.basic.mq.global;

import org.jboss.logging.Logger;
import org.springframework.util.ErrorHandler;
/**
 * 消费类专门用来处理错误异常的消息
 * @author Mr.Cheng
 *
 */
public class MessageErrorHandler implements ErrorHandler{

	  private static final Logger logger = Logger.getLogger(MessageErrorHandler.class);  
      
	    public void handleError(Throwable t) {  
	        logger.error("RabbitMQ happen a error:" + t.getMessage(), t);  
	    }  

}

package org.cs.basic.mq.global;

import java.io.IOException;
/**
 * 序列化和反序列化工厂
 * @author Mr.Cheng
 *
 */
public interface CodecFactory {
	  byte[] serialize(Object obj) throws IOException;  
	  Object deSerialize(byte[] in) throws IOException;
}

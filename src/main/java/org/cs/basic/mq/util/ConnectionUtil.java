package org.cs.basic.mq.util;

import java.io.IOException;

import org.cs.basic.mq.global.EventControlConfig;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
/**
 * 连接工具类
 * @author Mr.Cheng
 * @since 2016/11/29 15:55
 */
public class ConnectionUtil {
	private ConnectionFactory factory;
	private Connection connection;
	public ConnectionUtil(EventControlConfig config){
		if(factory==null){
			synchronized (ConnectionUtil.class){
				if(null == factory){
			    factory = new ConnectionFactory();
				factory.setHost(config.getServerHost());
				factory.setUsername(config.getUsername());
				factory.setPassword(config.getPassword());
				factory.setPort(config.getPort());
				}
			}
		}
	}
	public Connection getConnection() throws IOException{
		if(connection==null){
			synchronized (ConnectionUtil.class){
				if(connection==null){
					connection=factory.newConnection();
				}
			}
		}
		return connection;
	}
	public Channel getChannel() throws IOException{
		if(connection==null){
			getConnection();
		}
		return connection.createChannel();
	}
	
	public void close() throws IOException{
		if(connection!=null){
			connection.close();
		}
	}

}

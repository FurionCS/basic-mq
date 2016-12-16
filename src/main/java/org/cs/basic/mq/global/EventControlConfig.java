package org.cs.basic.mq.global;
/**
 * 专门配置和rabbitmq通信的一些信息，比如地址，端口等信息
 * @author Mr.Cheng
 *
 */
public class EventControlConfig {
	   private final static int DEFAULT_PORT = 5672;  
	      
	    private final static String DEFAULT_USERNAME = "guest";  
	      
	    private final static String DEFAULT_PASSWORD = "guest";  
	      
	    private final static int DEFAULT_PROCESS_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;  
	      
	    private static final int PREFETCH_SIZE = 1;  
	      
	    private String serverHost ;  
	      
	    private int port = DEFAULT_PORT;  
	      
	    private String username = DEFAULT_USERNAME;  
	      
	    private String password = DEFAULT_PASSWORD;  
	      
	    private String virtualHost;  
	      
	    /** 
	     * 和rabbitmq建立连接的超时时间 
	     */  
	    private int connectionTimeout = 0;  
	      
	    /** 
	     * 事件消息处理线程数，默认是 CPU核数 * 2 
	     */  
	    private int eventMsgProcessNum;  
	      
	    /** 
	     * 每次消费消息的预取值 
	     */  
	    private int prefetchSize;  
	      
	    public EventControlConfig(String serverHost) {  
	        this(serverHost,DEFAULT_PORT,DEFAULT_USERNAME,DEFAULT_PASSWORD,null,0,DEFAULT_PROCESS_THREAD_NUM,DEFAULT_PROCESS_THREAD_NUM,new HessionCodecFactory());  
	    }  
	    public EventControlConfig(String serverHost,String username,String password){
	    	 this(serverHost,DEFAULT_PORT,username,password,null,0,DEFAULT_PROCESS_THREAD_NUM,DEFAULT_PROCESS_THREAD_NUM,new HessionCodecFactory());  
	    }
	    public EventControlConfig(String serverHost,int port,String username,String password){
	    	 this(serverHost,port,username,password,null,0,DEFAULT_PROCESS_THREAD_NUM,DEFAULT_PROCESS_THREAD_NUM,new HessionCodecFactory());  
	    }
	  
	    public EventControlConfig(String serverHost, int port, String username,  
	            String password, String virtualHost, int connectionTimeout,  
	            int eventMsgProcessNum,int prefetchSize,CodecFactory defaultCodecFactory) {  
	        this.serverHost = serverHost;  
	        this.port = port>0?port:DEFAULT_PORT;  
	        this.username = username;  
	        this.password = password;  
	        this.virtualHost = virtualHost;  
	        this.connectionTimeout = connectionTimeout>0?connectionTimeout:0;  
	        this.eventMsgProcessNum = eventMsgProcessNum>0?eventMsgProcessNum:DEFAULT_PROCESS_THREAD_NUM;  
	        this.prefetchSize = prefetchSize>0?prefetchSize:PREFETCH_SIZE;  
	    }  
	  
	    public String getServerHost() {  
	        return serverHost;  
	    }  
	  
	    public int getPort() {  
	        return port;  
	    }  
	  
	    public String getUsername() {  
	        return username;  
	    }  
	  
	    public String getPassword() {  
	        return password;  
	    }  
	  
	    public String getVirtualHost() {  
	        return virtualHost;  
	    }  
	  
	    public int getConnectionTimeout() {  
	        return connectionTimeout;  
	    }  
	  
	    public int getEventMsgProcessNum() {  
	        return eventMsgProcessNum;  
	    }  
	  
	    public int getPrefetchSize() {  
	        return prefetchSize;  
	    }  
	  
}

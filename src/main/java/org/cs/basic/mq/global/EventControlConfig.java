package org.cs.basic.mq.global;
/**
 * 专门配置和rabbitmq通信的一些信息，比如地址，端口等信息
 * @author Mr.Cheng
 *
 */
public class EventControlConfig {
		/**
		 * 默认端口号
		 */
	   private final static int DEFAULT_PORT = 5672;  
	     /**
	      * 默认账号
	      */
	    private final static String DEFAULT_USERNAME = "guest";  
	     /**
	      * 默认密码
	      */
	    private final static String DEFAULT_PASSWORD = "guest";  
	     /**
	      * 多少个线程在消费  默认为为cpu核数*2
	      */
	    private final static int DEFAULT_PROCESS_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;  
//	    private final static int DEFAULT_PROCESS_THREAD_NUM =1;  
   
	    /**
	     * 每次从队列中取几条，只有等ack了才重新取
	     */
	    private static final int PREFETCH_SIZE = 1;  
	    /**
	     * ip
	     */
	    private String serverHost ;  
	     /**
	      * 端口号
	      */
	    private int port = DEFAULT_PORT;  
	    /**
	     * 账号
	     */
	    private String username = DEFAULT_USERNAME;  
	    /**
	     * 密码
	     */
	    private String password = DEFAULT_PASSWORD;  
	     /**
	      * 虚拟主机，推荐不同模块不同虚拟主机，实验得知：虚拟主机数量越多会提高QPS（即处理性能）
	      */
	    private String virtualHost;  
	      
	    /** 
	     * 和rabbitmq建立连接的超时时间 
	     */  
	    private int connectionTimeout = 0;  
	      
	    /** 
	     * 事件消息处理线程数，对应，默认是 CPU核数 * 2 
	     */  
	    private int eventMsgProcessNum;  
	      
	    /** 
	     * 每次消费消息的预取值 
	     */  
	    private int prefetchSize;  
	     /**
	      * 
	      * @param serverHost ip
	      */
	    public EventControlConfig(String serverHost) {  
	        this(serverHost,DEFAULT_PORT,DEFAULT_USERNAME,DEFAULT_PASSWORD,null,0,DEFAULT_PROCESS_THREAD_NUM,PREFETCH_SIZE,new HessionCodecFactory());  
	    } 
	    /**
	     * 
	     * @param serverHost  ip
	     * @param username
	     * @param password
	     */
	    public EventControlConfig(String serverHost,String username,String password){
	    	 this(serverHost,DEFAULT_PORT,username,password,null,0,DEFAULT_PROCESS_THREAD_NUM,PREFETCH_SIZE,new HessionCodecFactory());  
	    }
	    /**
	     * 
	     * @param serverHost  ip
	     * @param port        默认端口号：5672
	     * @param username    默认：guest
	     * @param password    默认：guest
	     * 
	     */
	    public EventControlConfig(String serverHost,int port,String username,String password){
	    	 this(serverHost,port,username,password,null,0,DEFAULT_PROCESS_THREAD_NUM,PREFETCH_SIZE,new HessionCodecFactory());  
	    }
	    /**
	     * 
	     * @param serverHost
	     * @param username
	     * @param password 
	     * @param processThreadNum   线程处理数，默认cpu核数*2
	     * @param prefetchSize       预取消息数量，默认为1 
	     */
	    public EventControlConfig(String serverHost,String username,String password,int processThreadNum,int prefetchSize){
	    	 this(serverHost,DEFAULT_PORT,username,password,null,0,processThreadNum,prefetchSize,new HessionCodecFactory());  
	    }
	    /**
	     * 
	     * @param serverHost
	     * @param username
	     * @param password 
	     * @param prefetchSize       预取消息数量，默认为1 ,个人推荐使用这个方法
	     */
	    public EventControlConfig(String serverHost,String username,String password,int prefetchSize){
	    	 this(serverHost,DEFAULT_PORT,username,password,null,0,DEFAULT_PROCESS_THREAD_NUM,prefetchSize,new HessionCodecFactory());  
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

package org.cs.basic.mq.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectAndByte {
	  public static Object ByteToObject(byte[] bytes) {  
		  Object obj = null;  
		  try {  
		      // bytearray to object  
		      ByteArrayInputStream bi = new ByteArrayInputStream(bytes);  
		      ObjectInputStream oi = new ObjectInputStream(bi);  
		    
		      obj = oi.readObject();  
		      bi.close();  
		      oi.close();  
		  } catch (Exception e) {  
		      System.out.println("translation" + e.getMessage());  
		      e.printStackTrace();  
		  }  
		         return obj;  
		}  
	  public static byte[] ObjectToByte(java.lang.Object obj) {  
		    byte[] bytes = null;  
		    try {  
		        // object to bytearray  
		        ByteArrayOutputStream bo = new ByteArrayOutputStream();  
		        ObjectOutputStream oo = new ObjectOutputStream(bo);  
		        oo.writeObject(obj);  
		  
		        bytes = bo.toByteArray();  
		  
		        bo.close();  
		        oo.close();  
		    } catch (Exception e) {  
		        System.out.println("translation" + e.getMessage());  
		        e.printStackTrace();  
		    }  
		    return bytes;  
		}  
}

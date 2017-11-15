 
package com.youlun.baseframework.util;

import java.security.MessageDigest;

public class MD5 {
 
	 public static String encrypt(String str) {
	
		 byte[] digesta = null;
		 String md5 = ""; 
		 try {
			 MessageDigest alga = MessageDigest.getInstance("MD5");
			 alga.update(str.getBytes());
			 digesta = alga.digest();
			 String[] Digit = { "0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
			 for(int i = 0; i < digesta.length ; i ++) {
				 md5 += Digit[(digesta[i] & 0XF0)/16];
				 md5 += Digit[digesta[i] & 0X0F];
			 }
		 }catch(Exception e){
			 md5 = "";
		 }
		 return md5;
		
	 }
	 public static void  main(String[] args){
		 System.out.print(encrypt("1"));
	 }
} 
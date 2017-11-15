package com.youlun.baseframework.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteUtils {
	
	/**
	 * 
	 * @param in
	 * @param flag
	 * @param out 出于效率考虑
	 * @return
	 * @throws IOException
	 */
	public static byte[] readToByte(InputStream in,byte flag,ByteArrayOutputStream out)
	{ 
		try
		{
			 out.reset();
			 int data = -1; 
			 while(true)
			 {
				  data = in.read();
				  if(data==-1||data==flag)
				  {
					  break;
				  }
				  out.write(data); 
				
			 }
			 return out.toByteArray();
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}
	
 
	public static byte[] readToByte(InputStream in,byte[] flags) throws IOException
	{ 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return readToByte(in,flags,out);
	}
	  
	public static byte[] readToByte(InputStream in,byte[] flags,ByteArrayOutputStream out)
	{ 
		try
		{
			 out.reset();
			 int data = -1; 
			 byte[] curFlags = new byte[flags.length];
			 int curIndex = 0;
			 boolean flag = false;
			 while(true)
			 {
				  data = in.read();
				  if(data==-1)
				  {
					  if(flag)
					  {
						  for(int i=0;i<curFlags.length;i++)
						  {
							  out.write(curFlags[(i+curIndex)%curFlags.length]);
						  }
					  }else
					  {
						  if(curIndex>0)
						  {
							  for(int i=0;i<curIndex;i++)
							  {
								  out.write(curFlags[i]);
							  }
						  }
					  }
					  break;
				  }
				 
				  //填满字节后开始循环才写数据
				  if(curIndex>= flags.length)
				  {
					  curIndex = curIndex%flags.length; 
					  flag = true;
				  }
				  if(flag) //填满字节后开始循环才写数据
				  {
					  out.write(curFlags[curIndex]);
				  }
				  curFlags[curIndex] = (byte) data;
				   
				  if(equals(flags,0,curFlags,curIndex+1))
				  {
					  break;
				  } 
				  curIndex = curIndex+1; 
			 }
			 return out.toByteArray();
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}  
	
	public static boolean equals(byte[] data1,int begin1,byte[] data2,int begin2)
	{
		if(data1==null||data2==null||data1.length!=data2.length)
		{
			return false;
		}
		int index1;
		int index2;
		for(int i=0;i<data1.length;i++)
		{
			index1 = (i+begin1)%data1.length;
			index2 = (i+begin2)%data2.length;
			if(data1[index1]!=data2[index2])
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean equals(byte[] data1,byte[] data2)
	{
		if(data1==null||data2==null||data1.length!=data2.length)
		{
			return false;
		}
		for(int i=0;i<data1.length;i++)
		{
			if(data1[i]!=data2[i])
			{
				return false;
			}
		}
		return true;
	} 
	
	public static byte[] readToByte(InputStream in,byte flag)
	{
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 return readToByte(in,flag,out);
	}

}

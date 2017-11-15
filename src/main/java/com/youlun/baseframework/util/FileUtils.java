package com.youlun.baseframework.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	
	
	public static void saveToFile(String strData,String path)
	{
		saveToFile(strData,path,null);
	}
	
	public static void saveToFile(String strData,String path,boolean override)
	{
		saveToFile(strData,path,null,override);
	}
	
	public static void saveToFile(String strData,String path,String charSet,boolean override)
	{
		try
		{
			byte[] data = null;
			if(charSet==null)
			{
				data = strData.getBytes();
			}else
			{
				data = strData.getBytes(charSet);
			} 
			saveToFile(data,path,override);
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
	} 
	public static void saveToFile(String strData,String path,String charSet)
	{
		saveToFile(strData,path,charSet,false);
	}
	 
	
	public static void saveToFile(byte[] data,String path)  
	{
		saveToFile(data,0,data.length,path);
	}
	public static void saveToFile(byte[] data,String path,boolean override)  
	{
		saveToFile(data,0,data.length,path,override);
	}

	public static void saveToFile(byte[] data,int begin,int len,String path,boolean override)
	{
		saveToFile(data,begin,len,new File(path),override);
	}
	public static void saveToFile(byte[] data,int begin,int len,String path)
	{
		saveToFile(data,begin,len,new File(path),false);
	}

	public static void saveToFile(byte[] data,File file) throws IOException
	{
		saveToFile(data,0,data.length,file,false);
	}
	
	public static void saveToFile(byte[] data,File file,boolean override) throws IOException
	{
		saveToFile(data,0,data.length,file,override);
	}
	
	public static void saveToFile(byte[] data,int begin,int len,File file,boolean override)
	{
		if(override)
		{
			if(file.exists())
			{
				file.delete();
			}
		}
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			out.write(data,begin,len);
			out.flush();
		}catch(Exception ex){
			ExceptionUtils.throwException(ex);
		}finally
		{
			close(out);
		} 
	} 
	
	public static String bytesToStr(byte[] data,String charSet)
	{
		try
		{
			int beginIndex = 0;
			if(data.length>3)
			{
				if(data[0]==-17&&data[1]==-69&&data[2]==-65)
				{
					beginIndex = 3;
					if(charSet==null)
					{
						charSet = "UTF-8";
					}
				}
			} 
			if(charSet!=null)
			{
				return new String(data,beginIndex,data.length-beginIndex,charSet); 
			}else 
			{
				return new String(data,beginIndex,data.length-beginIndex); 
			}
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}
		return null;
	}

	public static void saveToFile(InputStream in,File file,boolean ovveride)
	{
		OutputStream out = null;
		try
		{
			if(ovveride)
			{
				if(file.exists())
				{
					file.delete();
				}
			}
			out = new FileOutputStream(file);
			byte[] tData = new byte[1024];
			int len = 0;
			while(true)
			{
				len = in.read(tData);
				if(len==-1)
				{
					break;
				} 
				out.write(tData, 0, len);
			}
			out.flush();
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}finally 
		{
			FileUtils.close(out);
		}
		
	}  
	public static void saveToFile(InputStream in,File file)
	{
		saveToFile(in,file);
	}
	
	public static void inToOut(InputStream in,OutputStream out)
	{
		int len = 0;
		byte[] data = new byte[1024];
		try
		{
			while(true)
			{
				len = in.read(data);
				if(len==-1)
				{
					break;
				}
	
				out.write(data, 0, len);
			}
		}catch(Exception ex)
		{
			ExceptionUtils.throwException(ex);
		}finally{
			FileUtils.close(in);
		}
	}
	
	public static byte[] readData(InputStream in)
	{
		  try
		  {
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    byte[] temp = new byte[2048];
		    int len = 0;
		    while(true)
		    {
		    	len = in.read(temp);
		    	if(len==-1)
		    	{
		    		break;
		    	}
		    	out.write(temp, 0, len);
		    }
		    return out.toByteArray();
		  }catch(Exception ex)
		  {
			  ExceptionUtils.throwException(ex);
		  }
		  
		  return null;
		 
	}
	
	public static String getFileExtents(File file)
	{
		String name = file.getName();
		return name.substring(name.lastIndexOf(".")+1);
	}
	
	public static String getFileExtents(String file)
	{
		return getFileExtents(new File(file));
	}
	
	public static byte[] readFile(String path)
	{
		 return readFile(new File(path));
	}
	
	public static byte[] readFile(File file)
	{ 
		FileInputStream in = null;
		try
		{
			in = new FileInputStream(file); 
			return readData(in);
		    
		}catch(Exception ex){
			ExceptionUtils.throwException(ex);
		}finally
		{
			close(in);
		} 
		return null;
	}
	
	public static void close(AutoCloseable in)
	{
		
		if(in!=null)
		{  
			try {
				in.close();
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ExceptionUtils.handler(ex);
			} 
		}
	}
	 
	
	
	public static String  readFileToStr()
	{
		return null;
	}
	 
}

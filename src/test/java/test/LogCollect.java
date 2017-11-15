package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket; 
import org.apache.log4j.net.SocketServer;

public class LogCollect {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub 
//	    SocketServer server = new SocketServer(new File("."));
//	    server.main(args); 
	    SocketServer.main(new String[]{"9988","D:/Log4jConfig/log4j.properties","D:/Log4jConfig"});
	}
	
	public static void myServer() throws Exception
	{
		 ServerSocket server = new ServerSocket(9988);
		    Socket client = null;
		    try
		    {
			    client = server.accept();
			    byte[] data = new byte[4096];
			    InputStream in = client.getInputStream();
			    int len = 0;
			    while(true)
			    {
			    	len = in.read(data);
			    	System.out.print(new String(data,0,len));
			    }
			   
		    }finally
		    {
		    	if(client!=null)
		    	{
		    		client.close();
		    	}
		    	server.close();
		    }
	}

}

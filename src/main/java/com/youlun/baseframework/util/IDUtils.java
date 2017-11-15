package com.youlun.baseframework.util;

import java.util.UUID;

public class IDUtils {
	
	public static String getID()
	{
		UUID uid = java.util.UUID.randomUUID();
		return uid.toString().replaceAll("-", "");
	}
	
	public static void main(String[] args)
	{
		System.out.println(getID());
		System.out.println(getID());
		System.out.println(getID());
		System.out.println(getID());
	}
}

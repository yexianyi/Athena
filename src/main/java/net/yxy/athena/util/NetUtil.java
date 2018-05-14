package net.yxy.athena.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetUtil {

	public static boolean isReachable(String addr) {
		try {
			InetAddress address = InetAddress.getByName(addr);
			System.out.println("Name: " + address.getHostName());
			System.out.println("Addr: " + address.getHostAddress());
			System.out.println("Reach: " + address.isReachable(3000));
			return true;
			
		} catch (UnknownHostException e) {
			System.err.println("Unable to lookup "+addr);
			return false ;
		} catch (IOException e) {
			System.err.println("Unable to reach "+addr);
			return false ;
		} 

	}
	
	
	public static String getHostName(String addr) {
		try {
			InetAddress address = InetAddress.getByName(addr);
			return address.getHostName();
			
		} catch (UnknownHostException e) {
			System.err.println("Unable to lookup "+addr);
		} catch (IOException e) {
			System.err.println("Unable to reach "+addr);
		}
		
		return null; 
	}
	
	
	public static void main(String ars[]) {
		System.out.println(NetUtil.isReachable("192.168.99.101")) ;
	}

}

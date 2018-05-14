package net.yxy.athena2.app;

import net.yxy.athena2.service.RedisService;

public class Starter {

	public static void main(String[] args) {
		//TODO: 1. Using JCoulds to launch node servers 
		//TODO: 2. Save node servers info into redis
		String[] servers = {"192.168.99.101","192.168.99.102","192.168.99.103"} ;
		RedisService rs = new RedisService() ;
		rs.initNodeServerInfo(servers);
		
		//3. Introspection:
		//3.1 assign ping test task to each node server and sort response time
		
	}

}

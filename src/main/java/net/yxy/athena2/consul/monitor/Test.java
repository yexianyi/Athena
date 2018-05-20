package net.yxy.athena2.consul.monitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.NotRegisteredException;

public class Test {
	
	//consul agent -enable-script-checks=true -bind=192.168.1.10  -data-dir=e:\consul
	public static void main(String[] args)  {
		String consulServer = "192.168.1.13" ;
		
		HostAndPort hostAndPort = HostAndPort.fromParts("192.168.1.10", 8500) ;
		//Join consul clustering
		Consul consul = Consul.builder().withHostAndPort(hostAndPort).build(); // connect to Consul on localhost
		AgentClient agentClient = consul.agentClient();
		agentClient.join(consulServer) ;
		
		//Register and check your service in with Consul.
//		String serviceName = "MyTomcatService";
//		String serviceId = "1";
//		Map<String, String> metas = new HashMap<String, String>() ;
//		metas.put("tomcat_version", "7.2") ;
//		agentClient.register(8080, 5L, serviceName, serviceId, Arrays.asList("primary","v2"),  metas); // registers with a TTL of 3 seconds
//		try {
//			agentClient.pass(serviceId);
//		} catch (NotRegisteredException e) {
//			e.printStackTrace();
//		} // check in with Consul, serviceId required only.  client will prepend "service:" for service level checks.
		// Note that you need to continually check in before the TTL expires, otherwise your service's state will be marked as "critical".
		
//		HealthClient healthClient = consul.healthClient();
//		// discover only "passing" nodes
//		List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances("MyService").getResponse(); 
		
//		ConsulClient client = new ConsulClient("localhost");
//		Response<Void> response = client.agentJoin(consulServer, false) ;
		
		// register new service
//		NewService newService = new NewService();
//		newService.setId("myapp_01");
//		newService.setName("myapp");
//		newService.setAddress("192.168.1.10");
//		newService.setTags(Arrays.asList("EU-West", "EU-East"));
//		newService.setPort(8080);
//		System.out.println(response.toString()) ;
		

	}

}

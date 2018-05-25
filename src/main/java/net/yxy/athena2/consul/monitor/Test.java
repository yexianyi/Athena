package net.yxy.athena2.consul.monitor;

import java.util.Arrays;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;

public class Test {
	
	//consul agent -enable-script-checks=true -bind=192.168.1.10  -data-dir=e:\consul
	public static void main(String[] args)  {
		String consulServer = "192.168.1.13" ;
		
		
//		HealthClient healthClient = consul.healthClient();
//		// discover only "passing" nodes
//		List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances("MyService").getResponse(); 
		
		ConsulClient client = new ConsulClient("localhost");
		Response<Void> response = client.agentJoin(consulServer, false) ;
		System.out.println(response.toString());
		
		// register new service
//		NewService newService = new NewService();
//		newService.setId("myapp_01");
//		newService.setName("myapp");
//		newService.setAddress("192.168.1.10");
//		newService.setTags(Arrays.asList("EU-West", "EU-East"));
//		newService.setPort(8080);
//		Response<Void> response = client.agentServiceRegister(newService) ;
//		System.out.println(response.toString()) ;
		

	}

}

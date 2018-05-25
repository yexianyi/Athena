package net.yxy.athena2.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;

import net.yxy.athena2.service.NodeServerService;

public class ServiceMonitor extends Thread{
	private Set<String> preServices = new HashSet<String>()  ;
	private NodeServerService nss = new NodeServerService() ;
	private Consul consul ;

	public ServiceMonitor(Consul consul){
		this.consul = consul ;
	}
	
	
	@Override
	public void run() {
		Set<String> currServices = new HashSet<String>() ;
		CatalogClient client = consul.catalogClient() ;
		ConsulResponse<Map<String, List<String>>> map = client.getServices() ;
		Iterator<Entry<String, List<String>>> entries = map.getResponse().entrySet().iterator(); 
		while (entries.hasNext()) { 
		  Entry<String, List<String>> entry = entries.next(); 
		  String serviceName = entry.getKey() ;
//		  List<String> tags =  entry.getValue() ;
//		  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		  currServices.add(serviceName) ;
		}
		
		Collection<String> C = new HashSet<String>(preServices);
	    C.retainAll(currServices);
	    
	    //new services collections
	    currServices.removeAll(C);
	    addNewServices(currServices) ;

	    //offline service collections
	    preServices.removeAll(C);
	    removeOfflineServices(preServices) ;

	}
	
	private void removeOfflineServices(Set<String> services) {
		//do nothing else for now
		System.out.println("Removed Services:");
		for(String service:services) {
			System.out.println("	"+service) ;
		}
	}


	private void addNewServices(Set<String> services) {
		//do nothing else for now
		System.out.println("Added Services:");
		for(String service:services) {
			System.out.println("	"+service) ;
		}
	}


	public static void main(String[] args) {
		Consul consul = Consul.builder().build();
		Thread thread = new ServiceMonitor(consul) ;
		thread.start();

	} 
	
}

package net.yxy.athena2.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;

import net.yxy.athena.global.Constants;
import net.yxy.athena2.service.NodeServerService;

@Component
public class ServiceMonitor implements DisposableBean, Runnable{
	private final Logger logger = LoggerFactory.getLogger(ServiceMonitor.class);
	
	@Resource
	private NodeServerService nss ;
	
	private Consul consul ;
	private Set<String> preServices = new HashSet<String>()  ;
	private Thread thread;
	private volatile boolean isShutdown = false ;
	
	public ServiceMonitor(){
		this.consul = Consul.builder().build();
		this.thread = new Thread(this);
	    this.thread.start();
	}
	
	
	@Override
	public void run() {
		logger.info("Service Monitor is started");
		
		while(!Thread.currentThread().isInterrupted() && !isShutdown) {
			Set<String> currServices = new HashSet<String>() ;
			CatalogClient client = consul.catalogClient() ;
			ConsulResponse<Map<String, List<String>>> map = client.getServices() ;
			Iterator<Entry<String, List<String>>> entries = map.getResponse().entrySet().iterator(); 
			while (entries.hasNext()) { 
			  Entry<String, List<String>> entry = entries.next(); 
			  String serviceName = entry.getKey() ;
//			  List<String> tags =  entry.getValue() ;
//			  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
			  currServices.add(serviceName) ;
			}
			
			// creating copy of Collection using copy constructor 
	        Collection<String> copy = new HashSet<String>(currServices);
			Collection<String> C = new HashSet<String>(preServices);
		    C.retainAll(currServices);
		    
		    //new services collections
		    currServices.removeAll(C);
		    addNewServices(currServices) ;

		    //offline service collections
		    preServices.removeAll(C);
		    removeOfflineServices(preServices) ;
		    
		    //reset preServices
		    preServices.clear();
		    preServices.addAll(copy) ;
		    
		    try {
				Thread.currentThread().sleep(Constants.SYN_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	private void removeOfflineServices(Set<String> services) {
		//do nothing else for now
		if(services.size()>0) {
			for(String service:services) {
				logger.info("Removed Service:"+service) ;
			}
		}
	}


	private void addNewServices(Set<String> services) {
		//do nothing else for now
		if(services.size()>0) {
			for(String service:services) {
				logger.info("Added Service:"+service) ;
			}
		}
	}


	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}


	
}

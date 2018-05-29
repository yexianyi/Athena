package net.yxy.athena2.monitor;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.orbitz.consul.Consul;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.model.kv.Value;
import com.yxy.chukonu.docker.service.SystemService;

import net.yxy.athena2.service.NodeServerService;

public class NodeStateMonitor implements DisposableBean, Runnable {
	private final Logger logger = LoggerFactory.getLogger(NodeStateMonitor.class);
	
	private SystemService ss ;
	private NodeServerService nss = new NodeServerService() ;
	private volatile static boolean isShutdown = false ;
	
	private Thread thread;

    public NodeStateMonitor(){
        this.thread = new Thread(this);
        this.thread.start();
    }
	
	private static void watchKVEvents(Consul consul) {
		KVCache kvCache = KVCache.newCache(consul.keyValueClient(), "test", 5); 
		kvCache.addListener(new ConsulCache.Listener<String, Value>() { 
			public void notify(Map<String, Value> newValues) { // Key changed 
				System.out.println("<--------------KV Event:-------------->");
				for (Entry<String, Value> entry : newValues.entrySet()) {
					System.out.println(entry.getKey().toString() + ":" + entry.getValue().toString());
				}
			} 
		});
		kvCache.start();
	}

	
	@Deprecated
	private void watchServiceEvents(Consul consul) {
		String serviceName = "MySQL_datasource";
		ServiceHealthCache svHealth = ServiceHealthCache.newCache(consul.healthClient(), serviceName);
		svHealth.addListener(new ConsulCache.Listener<ServiceHealthKey, ServiceHealth>() {
			@Override
			public void notify(Map<ServiceHealthKey, ServiceHealth> newValues) {
				System.out.println("<--------------Service Event:-------------->");
				for (Entry<ServiceHealthKey, ServiceHealth> entry : newValues.entrySet()) {
					System.out.println(entry.getKey().toString() + ":" + entry.getValue().toString());
				}
			}
		});
		svHealth.start();
		
	}
	
	

	@Override
	public void run() {
		Consul consul = Consul.builder().build(); // connect to Consul on localhost
		//watchKVEvents(consul) ;
		//TODO: need to think about how to monitor multi-docker machine with one single thread.
		NodeStateSynchronizer synchronizer = new NodeStateSynchronizer(null) ;
		
		while(!Thread.currentThread().isInterrupted() && !isShutdown) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}


	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

}

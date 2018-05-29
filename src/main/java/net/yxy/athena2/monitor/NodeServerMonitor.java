package net.yxy.athena2.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.orbitz.consul.Consul;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.NodesCatalogCache;
import com.orbitz.consul.model.health.Node;
import com.orbitz.consul.option.QueryOptions;

import net.yxy.athena.global.Constants;
import net.yxy.athena.global.NodeServerRole;
import net.yxy.athena2.model.entity.NodeServerEntity;
import net.yxy.athena2.service.DataSourceService;
import net.yxy.athena2.service.NodeServerService;

@Component
public class NodeServerMonitor implements DisposableBean, Runnable{
	private final Logger logger = LoggerFactory.getLogger(NodeServerMonitor.class);
	
	@Resource
	private NodeServerService nss ;
	
	@Resource
	private DataSourceService dss ;
	
	private Consul consul ;
	private Set<String> preNodes = new HashSet<String>()  ;
	private Thread thread;
	private volatile boolean isShutdown = false ;
	
	
	public NodeServerMonitor(){
		consul = Consul.builder().build();
		 this.thread = new Thread(this);
	     this.thread.start();
	}
	
	@Override
	public void run() {
		logger.info("Node Server Monitor is started");
		
		QueryOptions queryOptions = QueryOptions.BLANK ;
		NodesCatalogCache nodeCatalogCache = NodesCatalogCache.newCache(consul.catalogClient(), queryOptions, 5) ;
		nodeCatalogCache.addListener(new ConsulCache.Listener<String, Node>(){
			@Override
			public void notify(Map<String, Node> newValues) {
				Set<String> currNodes = new HashSet<String>() ;
				for (Entry<String, Node> entry : newValues.entrySet()) {
					String nodeName = entry.getKey() ;
					currNodes.add(nodeName) ;
				}
				
				Collection<String> copy = new HashSet<String>(currNodes);
				Collection<String> C = new HashSet<String>(preNodes);
			    C.retainAll(currNodes);
			    
			    //new services collections
			    currNodes.removeAll(C);
			    addNewNodes(currNodes) ;

			    //offline service collections
			    preNodes.removeAll(C);
			    removeOfflineNodes(preNodes) ;
			    
			    //reset nodes
			    preNodes.clear();
			    preNodes.addAll(copy) ;
				
			}});
		nodeCatalogCache.start();
		
		
		while(!Thread.currentThread().isInterrupted() && !isShutdown) ;
		
	}
	
	private void removeOfflineNodes(Set<String> nodes) {
		//remove node records from redis
		for(String nodeName : nodes) {
			nss.removeNodeServer(nodeName);
		}
		
		//remove node from each datasources candidates list, so that new request will not consider these offline nodes.
		for(String nodeName : nodes) {
			for(String dsName : dss.getAllDataSourceNames()) {
				dss.removeNodeServerFromDSNodeServerList(dsName, nodeName);
			}
		}
		
		for(String node:nodes) {
			logger.info("Removed node:"+node);
		}
		
	}


	private void addNewNodes(Set<String> nodes) {
		
		//add and init node to redis
		NodeServerEntity[] list = new NodeServerEntity[nodes.size()] ;
		int count = 0;
		for(String addr : nodes) {
			//TODO: need to init params
			NodeServerEntity node = new NodeServerEntity(addr, addr, NodeServerRole.Worker, addr, addr) ;
			list[count++] = node ;
		}
		nss.addNodeServersInfo(list);
			
		for(NodeServerEntity node:list) {
			logger.info("Added node:"+node.getName());
		}
	}
	


	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}


	
}

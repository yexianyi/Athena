package net.yxy.athena2.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.orbitz.consul.Consul;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.NodesCatalogCache;
import com.orbitz.consul.model.health.Node;
import com.orbitz.consul.option.QueryOptions;

import net.yxy.athena2.model.entity.NodeServerEntity;
import net.yxy.athena2.service.DataSourceService;
import net.yxy.athena2.service.NodeServerService;

public class NodeServerMonitor {
	private Set<String> preNodes = new HashSet<String>()  ;
	private NodeServerService nss = new NodeServerService() ;
	private DataSourceService dss = new DataSourceService() ;
	private Consul consul ;

	public NodeServerMonitor(Consul consul){
		this.consul = consul ;
	}
	
	
	public void start() {
		QueryOptions queryOptions = QueryOptions.BLANK ;
		NodesCatalogCache nodeCatalogCache = NodesCatalogCache.newCache(consul.catalogClient(), queryOptions, 5) ;
		nodeCatalogCache.addListener(new ConsulCache.Listener<String, Node>(){
			@Override
			public void notify(Map<String, Node> newValues) {
				System.out.println("<--------------Node Event:-------------->");
				Set<String> currNodes = new HashSet<String>() ;
				for (Entry<String, Node> entry : newValues.entrySet()) {
					String nodeName = entry.getKey() ;
					currNodes.add(nodeName) ;
				}
				
				Collection<String> C = new HashSet<String>(preNodes);
			    C.retainAll(currNodes);
			    
			    //new services collections
			    currNodes.removeAll(C);
			    addNewNodes(currNodes) ;

			    //offline service collections
			    preNodes.removeAll(C);
			    removeOfflineNodes(preNodes) ;
				
			}});
		nodeCatalogCache.start();
		
		
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
		
	}


	private void addNewNodes(Set<String> nodes) {
		//add and init node to redis
		NodeServerEntity[] list = new NodeServerEntity[nodes.size()] ;
		int count = 0;
		for(String addr : nodes) {
			//TODO: need to init params
			NodeServerEntity node = new NodeServerEntity(addr, addr, null, addr, addr) ;
			list[count++] = node ;
		}
		nss.addNodeServersInfo(list);
		
	}


	public static void main(String[] args) {

	} 
	
}

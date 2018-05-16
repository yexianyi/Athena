package net.yxy.athena2.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena.global.Constants;
import net.yxy.athena.global.NodeServerState;
import net.yxy.athena.util.NetUtil;

public class NodeServerService {

	private RedisDao dao = new RedisDao() ;
	
//	public void initNodeServerInfo(String[] addrs) {
//		for(String addr : addrs) {
//			//ping test
//			if(NetUtil.isReachable(addr)) {
//				NodeServerEntity server = new NodeServerEntity(NetUtil.getHostName(addr), addr, NodeServerState.Reachable, 100.0f, 100.0f, 0, "") ;
//				dao.insertSet(Constants.NODE_SERVERS.getBytes(), server);
//			}else {
//				//error log
//				NodeServerEntity server = new NodeServerEntity(addr, addr, NodeServerState.Not_reachable, 0.0f, 0.0f, 0,"Failed to ping test") ;
//				dao.insertSet(Constants.NODE_SERVERS.getBytes(), server);
//			}
//			
//		}
//	}
	
	
//	public Set<NodeServerEntity> getNodeServers(){
//		Set<NodeServerEntity> servers = (Set<NodeServerEntity>) dao.getSet(Constants.NODE_SERVER_KEY.getBytes()) ;
//		return servers ;
//	}
	
//	public boolean isExistServer(String addr) {
//		Set<NodeServerEntity> servers = getNodeServers() ;
//		for(NodeServerEntity s:servers) {
//			if(s.getAddr().equals(addr)) {
//				return true ;
//			}
//		}
//		
//		return false ;
//	}
	
//	public NodeServerEntity getServerByName(String sName) {
//		Set<NodeServerEntity> servers = getNodeServers() ;
//		for(NodeServerEntity s:servers) {
//			if(s.getAddr().equals(sName)) {
//				return s ;
//			}
//		}
//		
//		return null ;
//	}
	
	
	public void initNodeServerInfo(String[] addrs) {
		for(String addr : addrs) {
			String key = Constants.NODE_SERVER_KEY+addr ;
			//ping test
			if(NetUtil.isReachable(addr)) {
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_NAME_KEY, addr);
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_ADDR_KEY, addr);
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_STATUS_KEY, NodeServerState.Reachable.toString());
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_CPU_KEY, "100");
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_MEM_KEY, "100");
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_CONTAINER_NUM_KEY, "100");
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_STATUS_KEY, NodeServerState.Healthy.toString());
			}else {
				//error log
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_NAME_KEY, addr);
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_ADDR_KEY, addr);
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_STATUS_KEY, NodeServerState.Not_Reachable.toString());
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_CPU_KEY, "0");
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_MEM_KEY, "0");
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_CONTAINER_NUM_KEY, "100");
				dao.saveUpdateHashMap(key, Constants.NODE_SERVER_COMMENT_KEY, "Failed to connect.");
			}
			
		}
	}
	
	public Map<String,Map<String,String>> getAllNodeServers(){
		Map<String,Map<String,String>> servers = dao.getHashMaps(Constants.NODE_SERVER_KEY) ;
		return servers ;
	}
	
	public Map<String,Map<String,String>> getAvailableNodeServers(){
		Map<String, Map<String, String>> servers = getAllNodeServers() ;
		Iterator<Map.Entry<String, Map<String, String>>> iter = servers.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Map<String, String>> entry = iter.next();
			Map<String, String> attrMap = entry.getValue() ;
			if(attrMap.get(Constants.NODE_SERVER_STATUS_KEY).equalsIgnoreCase(NodeServerState.Not_Reachable.toString())) {
				iter.remove();
			}
		}
		return servers ;
	}
	
	
	public boolean isExistServer(String addr) {
		if(dao.getHashMap(Constants.NODE_SERVER_KEY+addr)==null) {
			return false ;
		}
		return true ;
	}
	
	
	public Map<String, String> getServerByName(String sName) {
		Map<String, String> map = dao.getHashMap(Constants.NODE_SERVER_KEY+sName) ;
		return map ;
	}

	public void saveUpdateNodeServer(String addr, String fieldKey, String fieldValue) {
		dao.saveUpdateHashMap(Constants.NODE_SERVER_KEY+addr, fieldKey, fieldValue);
	}
	
	public Set<String> getAllServerAddrs() {
		Set<String> addrs = new HashSet<String>() ;
		Set<String> nodeKeys = dao.getHashMapKeys(Constants.NODE_SERVER_KEY+"*") ;
		for(String key:nodeKeys) {
			addrs.add(key.replace(Constants.NODE_SERVER_KEY, "")) ;
		}
		
		return addrs ;
	}
	
	
	public Set<String> getAvailableServerAddrs() {
		Set<String> addrs = new HashSet<String>() ;
		Map<String, Map<String, String>> servers = getAllNodeServers() ;
		Iterator<Map.Entry<String, Map<String, String>>> iter = servers.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Map<String, String>> entry = iter.next();
			Map<String, String> attrMap = entry.getValue() ;
			if(!attrMap.get(Constants.NODE_SERVER_STATUS_KEY).equalsIgnoreCase(NodeServerState.Not_Reachable.toString())) {
				addrs.add(attrMap.get(Constants.NODE_SERVER_ADDR_KEY)) ;
			}
		}
		return addrs ;
	}

	
	public boolean isHealthy(String addr) {
		return dao.getHashMapValue(Constants.NODE_SERVER_KEY + addr, Constants.NODE_SERVER_STATUS_KEY)
				.equalsIgnoreCase(NodeServerState.Healthy.toString());
	}
	
}

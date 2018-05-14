package net.yxy.athena2.service;

import java.util.Set;

import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena.global.Constants;
import net.yxy.athena.global.NodeServerState;
import net.yxy.athena.util.NetUtil;
import net.yxy.athena2.model.entity.NodeServerEntity;

public class RedisService {
	private RedisDao dao = new RedisDao() ;
	
	public void initNodeServerInfo(String[] addrs) {
		for(String addr : addrs) {
			//ping test
			if(NetUtil.isReachable(addr)) {
				NodeServerEntity server = new NodeServerEntity(NetUtil.getHostName(addr), addr, NodeServerState.Reachable, "") ;
				dao.insertSet(Constants.NODE_SERVERS.getBytes(), server);
			}else {
				//error log
				NodeServerEntity server = new NodeServerEntity(addr, addr, NodeServerState.Not_reachable, "Failed to ping test") ;
				dao.insertSet(Constants.NODE_SERVERS.getBytes(), server);
			}
			
		}
	}
	
	public Set<NodeServerEntity> getNodeServers(){
		Set<NodeServerEntity> servers = (Set<NodeServerEntity>) dao.getSet(Constants.NODE_SERVERS.getBytes()) ;
		return servers ;
	}


}

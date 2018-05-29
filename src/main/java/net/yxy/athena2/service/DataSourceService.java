package net.yxy.athena2.service;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yxy.chukonu.docker.client.conn.DockerConnection;
import com.yxy.chukonu.docker.service.SwarmService;
import com.yxy.chukonu.docker.service.SystemService;
import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena.db.DataSourceStatus;
import net.yxy.athena.global.Constants;
import net.yxy.athena.util.MathUtil;
import redis.clients.jedis.Tuple;

@Service
public class DataSourceService {
	private SwarmService swarm ;
	private SystemService ss ;
	private NodeServerService nss = new NodeServerService() ;
	private RedisDao dao = new RedisDao() ;
	
	
	/**
	 * this method will be run in an independent thread.
	 * @param dsName
	 * @param dsType
	 * @param connMap
	 */
	@Async
	public void handleConnectionRequest(String dsName, String dsType, Map<String, String> connMap) {
		//1.check if any existing Node Server Candidates for current datasource
		if(!isExistNodeServerListForDS(dsName)) {
			initNodeServerCandidates(dsName, dsType, connMap) ;
		}
		
		String serverAddr = findBestNodeServer(dsName) ;
		runTask(serverAddr, dsName, dsType, connMap) ;
	}
	
	private boolean isExistNodeServerListForDS(String dsName) {
		return dao.isExistSortedSet(Constants.DATA_SOURCE_KEY+dsName) ;
	}
	
	
	private void initNodeServerCandidates(String dsName, String dsType, Map<String, String> connMap) {
		Set<String> nodeServerAddrs = nss.getAvailableServerAddrs() ;
		for(String addr : nodeServerAddrs) {
			//TODO: send rest request to each node server to get delay duration.
			float delay = (float) Math.random() ;
			saveAndUpdateDSNodeServerList(dsName, delay, addr);
		}//end for
		
		
	}
	
	private void saveAndUpdateDSNodeServerList(String dsName, float delay, String addr) {
		dao.insertSortedSet(Constants.DATA_SOURCE_KEY+dsName, delay, addr);
	}
	
	
	public void removeNodeServerFromDSNodeServerList(String dsName, String addr) {
		dao.removeRecord(Constants.DATA_SOURCE_KEY+dsName, addr) ;
	}
	
	
	public Set<String> getAllDataSourceNames() {
		return dao.getKeys(Constants.DATA_SOURCE_KEY) ;
	}
	
	
	/**
	 * Choose best node server to be set up container
	 * Strategy: 
	 * 		1. response delay time  
	 * 		2. CPU usage percentage
	 * 		3. If CPU usage >= 98%, then find 2nd shortest delay time server
	 * 		4. If all servers' CPU usage > 98%, then find a server randomly.
	 * @param dsName
	 * @return
	 */
	private String findBestNodeServer(String dsName) {
		Set<Tuple> candidates = dao.getSortedSetWithScore(Constants.DATA_SOURCE_KEY+dsName) ;
		float delaySum = 0.0f ;
		int count = 0 ;
		for(Tuple server:candidates) {
			count++ ;
			delaySum += server.getScore() ;
			
			DockerConnection conn = getDokcerConnection(server.getElement()) ;
			ss = new SystemService(conn) ;
			//get total cpu usage percent of current server
			float cpuPer = ss.getHostCpuUsage() ;
			try {
				if(cpuPer<0.98f) { //if has enough capability, assign task to this server
					return server.getElement() ;
				}else {//no enough capability, try next server
					continue ;
				}
			}finally {
				conn.close();
			}
		}
		
		return getSuboptimalNodeServer(dsName, candidates, delaySum/count);
	}

	private DockerConnection getDokcerConnection(String sName) {
		Map<String, String> attrs = nss.getServerByName(sName) ;
		return new DockerConnection(attrs.get(Constants.NODE_SERVER_ADDR_KEY), attrs.get(Constants.NODE_SERVER_DOCKER_CLIENT_PORT), attrs.get(Constants.NODE_SERVER_DOCKER_CLIENT_CERT));
	}

	private String getSuboptimalNodeServer(String dsName, Set<Tuple> servers, float avg) {
		int index = 0; 
		for(Tuple server:servers) {
			if(server.getScore()<=avg) {
				index++ ;
			}else {
				break ;
			}
		}
		
		return getRandomNodeServer(dsName, 0, index);
	}

	private String getRandomNodeServer(String dsName, int from, int to) {
		int pos = MathUtil.getRandomInt(from, to) ;
		return dao.getSortedSet(Constants.DATA_SOURCE_KEY+dsName, pos, pos).iterator().next();
	}

	
	
	private boolean runTask(String targetHost, String dsName, String dsType, Map<String, String> connMap) {
		updateDSStatus(dsName, Constants.DATA_SOURCE_SERVICE_NAME, dsName) ;
		updateDSStatus(dsName, Constants.DATA_SOURCE_SERVICE_STATUS, DataSourceStatus.PLANNING.toString()) ;
		
		try {
			System.out.println(dsName+":"+targetHost+"|"+dsType) ;
			DockerConnection conn = getDokcerConnection(targetHost) ;
			SwarmService swarm = new SwarmService(conn) ;
			String serviceId = swarm.createService(dsName, targetHost, getImageName(dsType), Arrays.asList("CONSUL_AGENT="+Constants.CONSUL_SERVER)) ;
			updateDSStatus(dsName, Constants.DATA_SOURCE_SERVICE_ID, serviceId) ;
		} catch (Exception e) {
			e.printStackTrace();
			updateDSStatus(dsName, Constants.DATA_SOURCE_ERROR_KEY, e.getMessage()) ;
			return false ;
		}
		updateDSStatus(dsName, Constants.DATA_SOURCE_SERVICE_STATUS, DataSourceStatus.CREATING.toString()) ;
		return true ;
	}
	

	private void updateDSStatus(String dsName, String key, String value) {
		dao.saveUpdateHashMap(Constants.DATA_SOURCE_INFO_KEY+dsName, key, value);
	}
	

	/**
	 * This function is simulating the process of getting docker image name in terms of datasource type.
	 * @param dsType
	 * @return
	 */
	private String getImageName(String dsType) {
		String imgName = "" ;
		switch(dsType) {
			default: imgName = "tomcat:7" ;
		}
		return imgName;
	}
}

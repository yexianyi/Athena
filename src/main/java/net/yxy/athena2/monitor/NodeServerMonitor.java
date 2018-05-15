package net.yxy.athena2.monitor;

import java.util.Map;
import java.util.Set;

import com.yxy.chukonu.docker.client.conn.DockerConnection;
import com.yxy.chukonu.docker.service.SystemService;
import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena.global.Constants;
import net.yxy.athena2.model.entity.NodeServerEntity;
import net.yxy.athena2.service.NodeServerService;

public class NodeServerMonitor implements Runnable {
	
	private DockerConnection connection ;
	private SystemService ss ;
	private NodeServerService nss = new NodeServerService() ;
	private volatile boolean isShutdown = false ;
	
	
	public NodeServerMonitor(DockerConnection conn) {
		connection = conn ;
		ss = new SystemService(connection) ;
	}

	@Override
	public void run() {
		
		while(!Thread.currentThread().isInterrupted() && !isShutdown) {
			if(nss.isExistServer(connection.getHost())){
				float currCpuUsage = ss.getHostCpuUsage() ;
				nss.saveUpdateNodeServer(connection.getHost(), Constants.NODE_SERVER_CPU_KEY, currCpuUsage+"");
				float currMemUsage = ss.getHostMemUsage() ;
				nss.saveUpdateNodeServer(connection.getHost(), Constants.NODE_SERVER_MEM_KEY, currMemUsage+"");
			}
			
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void shutdown(){
		isShutdown = true ;
		connection.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

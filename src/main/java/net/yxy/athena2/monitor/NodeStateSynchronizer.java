package net.yxy.athena2.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.yxy.chukonu.docker.client.conn.DockerConnection;
import com.yxy.chukonu.docker.service.SystemService;

import net.yxy.athena.global.Constants;
import net.yxy.athena2.service.NodeServerService;

public class NodeStateSynchronizer implements DisposableBean, Runnable {
	private final Logger logger = LoggerFactory.getLogger(NodeStateSynchronizer.class);
	
	private DockerConnection connection ;
	private SystemService ss ;
	private NodeServerService nss = new NodeServerService() ;
	private volatile boolean isShutdown = false ;
	
	
	public NodeStateSynchronizer(DockerConnection conn) {
		connection = conn ;
		ss = new SystemService(connection) ;
	}

	@Override
	public void run() {
		
		while(!Thread.currentThread().isInterrupted() && !isShutdown) {
			if(nss.isExistServer(connection.getHost())){
				if(nss.isHealthy(connection.getHost())) {
					float currCpuUsage = ss.getHostCpuUsage() ;
					nss.saveUpdateNodeServer(connection.getHost(), Constants.NODE_SERVER_CPU_KEY, currCpuUsage+"");
					float currMemUsage = ss.getHostMemUsage() ;
					nss.saveUpdateNodeServer(connection.getHost(), Constants.NODE_SERVER_MEM_KEY, currMemUsage+"");
				}
			}
			
			
			try {
				Thread.sleep(Constants.SYN_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void shutdown(){
		isShutdown = true ;
		connection.close();
	}



	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

}

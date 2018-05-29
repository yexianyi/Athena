package net.yxy.athena2.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.HealthCheck;

import net.yxy.athena.global.Constants;

@Service
public class ConsulService {
	
	private Consul consul ;
	
	public ConsulService() {
		consul = Consul.builder().build();
	}
	
	
	public com.orbitz.consul.model.health.Service findService(String dsName) {
		return consul.agentClient().getServices().get(Constants.DATA_SOURCE_KEY+dsName) ;
	}
	
	
	public List<HealthCheck> getHealthStatus(String serviceName) {
		ConsulResponse<List<HealthCheck>> response =consul.healthClient().getServiceChecks(Constants.DATA_SOURCE_KEY+serviceName) ;
		return response.getResponse() ;
	}
	

}

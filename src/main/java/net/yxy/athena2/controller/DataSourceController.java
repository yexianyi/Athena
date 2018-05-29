package net.yxy.athena2.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.orbitz.consul.model.health.HealthCheck;

import net.yxy.athena2.service.ConsulService;
import net.yxy.athena2.service.DataSourceService;

@RestController  
public class DataSourceController {
	
	private final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
	
	@Autowired
	DataSourceService dataSourceService;
	
	@Autowired
	ConsulService consulService;

	
	@GetMapping("/handle")
	public WebAsyncTask<String> handleRequest(String dsName, String dsType, Map<String, String>connMap) {
		logger.info("handleRequest:"+dsName+" | dsType="+dsType);
		WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(10000, new Callable<String>() {
			@Override
			public String call() throws Exception {
				com.orbitz.consul.model.health.Service dsService = consulService.findService(dsName) ;
				if(dsService==null) {//do not exist datasource service yet -> create one
					dataSourceService.createDataSource(dsName, dsType, connMap);
				}else {
					List<HealthCheck> healthChecks = consulService.getHealthStatus(dsName) ;
					//check each health check status and generate report
					for(HealthCheck check:healthChecks) {
						if(!check.getStatus().equalsIgnoreCase("healthy")) {
							return "ERROR:"+check.getOutput();
						}
					}
					
					//Health Check is pass.
					return dsService.toString() ;
				}
				
				
				
				return "SUCCESS";
			}
		});

		
		webAsyncTask.onCompletion(new Runnable() {
			@Override
			public void run() {
			}
		});

		webAsyncTask.onTimeout(new Callable<String>() {
			@Override
			public String call() throws Exception {
				throw new TimeoutException();
			}
		});
		
		logger.info("handleRequest:"+dsName+" | dsType="+dsType+" | DONE!");
		return webAsyncTask;
	}
	
	
	


	
}

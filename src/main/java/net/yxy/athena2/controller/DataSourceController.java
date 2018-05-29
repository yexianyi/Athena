package net.yxy.athena2.controller;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import net.yxy.athena.util.TimeUtil;
import net.yxy.athena2.service.DataSourceService;

@RestController  
public class DataSourceController {
	
	private final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
	
	@Autowired
	DataSourceService dataSourceService;

	
	@GetMapping("/handle")
	public WebAsyncTask<String> handleRequest(String datasource, String dsType, Map<String, String>connMap) {
		logger.info("handleRequest:"+datasource+" | dsType="+dsType);
		WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(10000, new Callable<String>() {
			@Override
			public String call() throws Exception {
				dataSourceService.handleConnectionRequest(datasource, dsType, connMap);
				
				
				return "";
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
		
		logger.info("handleRequest:"+datasource+" | dsType="+dsType+" | DONE!");
		return webAsyncTask;
	}
	
	
	


	
}

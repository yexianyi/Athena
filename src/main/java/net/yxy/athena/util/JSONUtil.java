package net.yxy.athena.util;

import net.yxy.athena.db.EmbeddedDBServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONUtil {
	
	private JSONUtil(){
		
	}
	
	private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

	public static String convertObj(Object obj){
		String jsonRsp = null ;
		ObjectMapper mapper = new ObjectMapper();
		//Object to JSON in String
		try {
			jsonRsp = mapper.writeValueAsString(obj);
			logger.debug(jsonRsp);
			
		} catch (JsonProcessingException e) {
			logger.error("Cannot convert object to JSON, because "+e.getMessage());
		} finally{
			return jsonRsp ;
		}
		
	}
}

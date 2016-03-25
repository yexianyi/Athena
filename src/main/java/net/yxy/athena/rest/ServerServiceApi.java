/**
 * Copyright (c) 2016, Xianyi Ye
 *
 * This project includes software developed by Xianyi Ye
 * yexianyi@hotmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.yxy.athena.rest;

import static net.yxy.athena.global.Constants.ENTITY_SERVER;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

import net.yxy.athena.db.EmbeddedDBServer;
import net.yxy.athena.global.Constants;

@Path("/admin/server")
public class ServerServiceApi {
	static private Logger logger = LoggerFactory.getLogger(ServerServiceApi.class);  
	
	@GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
	public Response listServers() {
		ODatabaseDocumentTx db = EmbeddedDBServer.acquire() ;
		String jsonRsp = "" ;
		List<String> records = new ArrayList<String>() ;
		for(ODocument record : db.browseClass(ENTITY_SERVER)){
			jsonRsp = record.toJSON() ;
			records.add(jsonRsp) ;
		}
		
		Response.ResponseBuilder response = Response.ok(records).type(MediaType.APPLICATION_JSON);
		
		
		///////////////HTTP Cache by Expire////////////////
		//Expires:
		//The Expires response header indicates the amount of time that the response entity should be cached. 
		//It is useful to set the expiration for data that is not going to change for a known time length. 
		//Browsers use this response header to manage their caches among other user agents.
		//The javax.ws.rs.core.Response.ResponseBuilder#expires() method can be used to set the Expires header.
//		Date expirationDate = new Date(System.currentTimeMillis() + 1000*60);
//		response.expires(expirationDate);
		
		///////////////HTTP Cache by Duration////////////////
		CacheControl cc = new CacheControl() ;
		cc.setMaxAge(Constants.REFRESH_INTERVAL);
		response.cacheControl(cc) ;
		
		
		///////////////HTTP Cache by E-tag////////////////

		
		//		logger.debug("Checking if there an Etag and whether there is a change in the order...");
//		EntityTag etag = computeEtagForOrder(list);
//		Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(etag);
//		if (responseBuilder != null) {
//			// Etag match
//			list.debug("Order has not changed..returning unmodified response code");
//			return responseBuilder.build();
//		}
//		list.debug("Returning full Order to the Client");
//		OrderDto orderDto = (OrderDto) beanMapper.map(order, OrderDto.class);
//		responseBuilder = Response.ok(orderDto).tag(etag);
//		return responseBuilder.build();
//		
		
		return response.build();
	}


}

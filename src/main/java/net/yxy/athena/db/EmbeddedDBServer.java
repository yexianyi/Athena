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
package net.yxy.athena.db;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

import net.yxy.athena.global.Constants;

public class EmbeddedDBServer {
	private static Logger logger = LoggerFactory.getLogger(EmbeddedDBServer.class); 
	
	private static OPartitionedDatabasePool dbPool = null;
	private static OServer server = null;
	private static volatile ServerStatus status ;
	
	public EmbeddedDBServer(){
		status = ServerStatus.OFFLINE ;
	}
	
	
	public static ServerStatus startup(){
		if(server==null){
			try {
				setStatus(ServerStatus.CREATING) ;
				logger.info("Creating Database Server...");
				
				server = OServerMain.create();
				// FILL THE OServerConfiguration OBJECT
				server.startup(new File("src/main/resources/db.config.xml"));
				server.activate();
				
				setStatus(ServerStatus.CREATED) ;
				logger.info("Database Server is Online.");
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		
		return getStatus() ;
	}
	
	public static ServerStatus shutdown(){
		if(server!=null && server.isActive() && getStatus()!=ServerStatus.SHUTDOWN){
			setStatus(ServerStatus.SHUTTING_DOWN) ;
			logger.info("Database Server is shutting down...");
			
			logger.info("Database Connection Pool is shutting down...");
			dbPool.close();
			logger.info("Database Connection Pool is terminated...");
			
			server.shutdown() ;
			setStatus(ServerStatus.SHUTDOWN) ;
			logger.info("Database Server is terminated.");
		}
		
		return getStatus() ;
	}
	
	public static void initConnectionPool(){
		dbPool = new OPartitionedDatabasePool(Constants.DB_PATH, Constants.DB_USERNAME, Constants.DB_PASSWORD, Constants.DB_MAX_POOL_SIZE);
		dbPool.setAutoCreate(true) ;
//		database = new ODatabaseDocumentTx(Constants.DB_PATH);
	}
	
	public static ODatabaseDocumentTx acquire(){
		return dbPool.acquire() ;
	}
	

	public static void importSeedData(){
		logger.debug("Importing seed data...");
		
		ODatabaseDocumentTx db = acquire() ;
		if(!db.getMetadata().getSchema().existsClass("Server")){
			db.getMetadata().getSchema().createClass("Server") ;
		}
		
		logger.debug("Seed data importing is done.");
	}
	
	
	public static ServerStatus getStatus(){
		return status ;
	}
	
	private static void setStatus(ServerStatus status){
		EmbeddedDBServer.status = status ;
	}
	

	public static void main(String[] args) throws InterruptedException, IOException {
		EmbeddedDBServer.startup() ;
		EmbeddedDBServer.initConnectionPool() ;
		EmbeddedDBServer.importSeedData();
		
//		EmbeddedDBServer.shutdown();
		
	}
	
	@Deprecated
	private void theWayToCreateObjectDB(){
		OPartitionedDatabasePool dbPool = new OPartitionedDatabasePool(
				Constants.DB_PATH, Constants.DB_USERNAME,
				Constants.DB_PASSWORD, Constants.DB_MAX_POOL_SIZE);
		dbPool.setAutoCreate(true);
		OObjectDatabaseTx db = new OObjectDatabaseTx(dbPool.acquire());

		db.getEntityManager().registerEntityClass(Person.class);
		Person personA = db.newInstance(Person.class);
		personA.setName("tennantA");
		db.save(personA);
		db.close();
	}
	
	static class Person{
		String name = "jack" ;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
	}

}

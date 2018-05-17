//package net.yxy.athena.monitor;
//
//import java.util.List;
//import java.util.Map;
//
//import org.jclouds.openstack.nova.v2_0.domain.Server;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import static net.yxy.athena.global.Constants.* ;
//import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
//import com.orientechnologies.orient.core.record.impl.ODocument;
//import com.orientechnologies.orient.core.sql.OCommandSQL;
//import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
//
//import net.yxy.athena.db.EmbeddedDBServer;
//import net.yxy.athena.global.Constants;
//import net.yxy.athena.service.server.ComputeService;
//import net.yxy.athena.util.JSONUtil;
//
//public class HostSynchronizer extends Thread {
//
//	static private Logger logger = LoggerFactory.getLogger(HostSynchronizer.class); 
//	private ComputeService cs = new ComputeService() ;
//	
//	private volatile boolean isShutdown = false ;
//	
//	@Override
//	public void run() {
//		
//		while(!Thread.currentThread().isInterrupted() && !isShutdown){
//			Map<String, Server> map = cs.listServers() ;
//			ODatabaseDocumentTx db = EmbeddedDBServer.acquire() ;
//			//syn records from remote to local
//			for(String serverId: map.keySet()){
//				String json = JSONUtil.convertObj(map.get(serverId)) ;
//				
//				OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select * from "+ENTITY_SERVER+" where id = ?");
//				List<ODocument> resultSet = db.command(query).execute(serverId);
//				
//				if(resultSet.size()>0){
//					OCommandSQL update = new OCommandSQL("update "+ENTITY_SERVER+" content "+json+" where id='"+serverId+"'") ;
//					db.command(update).execute() ;
//					logger.debug("Update Server#"+serverId);
//				}else{ // size == 0
//					OCommandSQL insert = new OCommandSQL("insert into "+ENTITY_SERVER+" content "+json) ;
//					db.command(insert).execute() ;
//					logger.debug("insert into Server content "+json);
//				}
//				
//			}
//			
//			//syn records from local to remote
//			for(ODocument server : db.browseClass(ENTITY_SERVER)){
//				if(!map.containsKey(server.field("id"))){
//					logger.debug("remove Server:"+server.field("id"));
//					server.delete();
//				}
//			}
//			
//			
//			try {
//				Thread.sleep(Constants.SYN_INTERVAL);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			if(db!=null){
//				db.close(); 
//			}
//		}//end while
//	
//		
//	}
//	
//	public void shutdown(){
//		isShutdown = true ;
//	}
//	
//	
//	public static void main(String[] args){
//		HostSynchronizer updater  = new HostSynchronizer() ;
//		Thread th = new Thread(updater) ;
//		th.start();
//	}
//
//}

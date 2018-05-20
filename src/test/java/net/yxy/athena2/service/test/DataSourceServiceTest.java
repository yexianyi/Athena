package net.yxy.athena2.service.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena.global.Constants;
import net.yxy.athena2.model.entity.NodeServerEntity;
import net.yxy.athena2.service.DataSourceService;
import net.yxy.athena2.service.NodeServerService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataSourceServiceTest extends BaseTest{
	
	private RedisDao dao = new RedisDao() ;
	private DataSourceService dss = null ;
	private NodeServerService nss = null ;
	
	@Before
	public void before() {
		dao.clearDB(); 
		dss = new DataSourceService() ;
		nss = new NodeServerService() ;
	}
	
	
	@Test
	public void Test1InitNodeServerInfo() {
		NodeServerEntity[] servers = {host1, host2, host3} ;
		nss.initNodeServerInfo(servers);
		
	}
	
	@Test
	public void test2HandleConnectionRequest() {
		NodeServerEntity[] servers = {host1, host2, host3} ;
		nss.initNodeServerInfo(servers);
		dss.handleConnectionRequest("mysql", Constants.DATA_SOURCE_TYPE_MYSQL, null);
		
	}
	
	
}

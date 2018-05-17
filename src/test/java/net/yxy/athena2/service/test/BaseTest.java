package net.yxy.athena2.service.test;

import net.yxy.athena.global.NodeServerRole;
import net.yxy.athena2.model.entity.NodeServerEntity;

public class BaseTest {
	
	protected NodeServerEntity host1 = new NodeServerEntity("vm1","192.168.99.101", NodeServerRole.Manager, "2376", "C:\\Users\\Administrator\\.docker\\machine\\machines\\vm1") ;
	protected NodeServerEntity host2 = new NodeServerEntity("vm2","192.168.99.102", NodeServerRole.Worker, "2376", "C:\\Users\\Administrator\\.docker\\machine\\machines\\vm2") ;
	protected NodeServerEntity host3 = new NodeServerEntity("vm3","192.168.99.103", NodeServerRole.Worker, "2376", "C:\\Users\\Administrator\\.docker\\machine\\machines\\vm3") ;
	

}

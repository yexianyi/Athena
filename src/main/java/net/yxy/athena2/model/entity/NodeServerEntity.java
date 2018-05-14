package net.yxy.athena2.model.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.yxy.athena.global.NodeServerState;

@Data @AllArgsConstructor
public class NodeServerEntity implements Serializable {
	
	private String name ;
	private String addr ;
	private NodeServerState status ;
	private String comment ;

}

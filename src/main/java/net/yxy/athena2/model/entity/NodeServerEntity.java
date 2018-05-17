package net.yxy.athena2.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.yxy.athena.global.NodeServerRole;

@Data @AllArgsConstructor
public class NodeServerEntity {
	
	private String name ;
	private String addr ;
	private NodeServerRole role ;
	private String port ;
	private String cert ;

}

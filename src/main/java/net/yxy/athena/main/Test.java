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
package net.yxy.athena.main;

import java.util.List;

import org.eclipse.jetty.util.security.Password;

public class Test {
	
	private List<String> list ;
	
	private String name = "test" ;
	
	private String password = "123456" ;
	
	

	public List<String> getList() {
		return list;
	}



	public void setList(List<String> list) {
		this.list = list;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public static void main(String[] args) {
		org.eclipse.jetty.util.security.Password.main(new String[]{"123456"});
	}

}

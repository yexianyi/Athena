/**
 * Copyright 2016 Xianyi Ye

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package net.yxy.athena.main;

import java.util.Collections;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * This class shows how to configure Authentication in programming method.
 * @author xianyiye
 * 2016/03/09
 */

public class AppMain2 {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		// Handler for multiple web apps
		HandlerCollection handlers = new HandlerCollection();

		// Creating the first web application context
		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("src/main/webapp");
		webapp1.setContextPath("/athena");
		
		
		// Configure LoginService which is required by each context/webapp 
		// that has a authentication mechanism, which is used to check the 
		// validity of the username and credentials collected by the 
		//authentication mechanism. Jetty provides the following implementations 
		// of LoginService:
		//		HashLoginService
		//		A user realm that is backed by a hash map that is filled either programatically or from a java properties file.
		//		JDBCLoginService
		//		Uses a JDBC connection to an SQL database for authentication
		//		DataSourceLoginService
		//		Uses a JNDI defined DataSource for authentication
		//		JAASLoginService
		//		Uses a JAAS provider for authentication, See the section on JAAS support for more information.
		//		SpnegoLoginService
		//		SPNEGO Authentication, See the section on SPNEGO support for more information.
        LoginService loginService = new HashLoginService("MyRealm", "src/main/resources/realm.properties");
        server.addBean(loginService);
		
		
		 // This constraint requires authentication and in addition that an
        // authenticated user be a member of a given set of roles for
        // authorization purposes.
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { "user", "admin" });
		
		// Binds a url pattern with the previously created constraint. The roles
        // for this constraing mapping are mined from the Constraint itself
        // although methods exist to declare and bind roles separately as well.
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);
		
        
        // A security handler is a jetty handler that secures content behind a
        // particular portion of a url space. The ConstraintSecurityHandler is a
        // more specialized handler that allows matching of urls to different
        // constraints. The server sets this as the first handler in the chain,
        // effectively applying these constraints to all subsequent handlers in
        // the chain.
        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		securityHandler.setConstraintMappings(Collections.singletonList(mapping));
		securityHandler.setAuthenticator(new FormAuthenticator("/logon.html","/logerror.html",false)); // BASIC/FORM AUTHMETHOD
		securityHandler.setLoginService(loginService);
		
		webapp1.setSecurityHandler(securityHandler);
		
		// do not need webdefault.xml. If uncomment this line, this file will be
		// load before each of web.xml files.
//		webapp1.setDefaultsDescriptor("src/main/webdefault/webdefault.xml");
		handlers.addHandler(webapp1);

		// Creating the second web application context
//		WebAppContext webapp2 = new WebAppContext();
//		webapp2.setResourceBase("src/main/webapp2");
//		webapp2.setContextPath("/webapp2");
//		webapp2.setDefaultsDescriptor("src/main/webdefault/webdefault.xml");
//		handlers.addHandler(webapp2);

		// Adding the handlers to the server
		server.setHandler(handlers);

		// Starting the Server
		server.start();
		System.out.println("Started!");
		server.join();


	}
}

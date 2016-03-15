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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * This class shows how to configure Authentication in programming method.
 * @author xianyiye
 * 2016/03/09
 */

public class AppMain2 {

	public static void main(String[] args) throws Exception {
		
		// Since this example shows off SSL configuration, we need a keystore
        // with the appropriate key. These lookup of jetty.home is purely a hack
        // to get access to a keystore that we use in many unit tests and should
        // probably be a direct path to your own keystore.
        File keystoreFile = new File("src/main/resources/keystore");
        if (!keystoreFile.exists())
        {
            throw new FileNotFoundException(keystoreFile.getAbsolutePath());
        }
		
		
        // Create a basic jetty server object without declaring the port. Since
        // we are configuring connectors directly we'll be setting ports on
        // those connectors.
		Server server = new Server();
		
		
		// HTTP Configuration
        // HttpConfiguration is a collection of configuration information
        // appropriate for http and https. The default scheme for http is
        // <code>http</code> of course, as the default for secured http is
        // <code>https</code> but we show setting the scheme to show it can be
        // done. The port for secured communication is also set here.
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        
        
        // HTTP connector
        // The first server connector we create is the one for http, passing in
        // the http configuration we configured above so it can get things like
        // the output buffer size, etc. We also set the port (8080) and
        // configure an idle timeout.
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        
        
        // SSL Context Factory for HTTPS
        // SSL requires a certificate so we configure a factory for ssl contents
        // with information pointing to what keystore the ssl connection needs
        // to know about. Much more configuration is available the ssl context,
        // including things like choosing the particular certificate out of a
        // keystore to be used.
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("OBF:19iy19j019j219j419j619j8"); //123456
        sslContextFactory.setKeyManagerPassword("OBF:19iy19j019j219j419j619j8"); //123456
		
        
        // HTTPS Configuration
        // A new HttpConfiguration object is needed for the next connector and
        // you can pass the old one as an argument to effectively clone the
        // contents. On this HttpConfiguration object we add a
        // SecureRequestCustomizer which is how a new connector is able to
        // resolve the https connection before handing control over to the Jetty
        // Server.
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());
 
        
        // HTTPS connector
        // We create a second ServerConnector, passing in the http configuration
        // we just made along with the previously created ssl context factory.
        // Next we set the port and a longer idle timeout.
        ServerConnector https = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(8443);
        https.setIdleTimeout(500000);
        
        
        // Here you see the server having multiple connectors registered with
        // it, now requests can flow into the server from both http and https
        // urls to their respective ports and be processed accordingly by jetty.
        // A simple handler is also registered with the server so the example
        // has something to pass requests off to.
 
        // Set the connectors
        server.setConnectors(new Connector[] {http, https});

		// Handler for multiple web apps
		HandlerCollection handlers = new HandlerCollection();

		// Creating the first web application context
		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("src/main/webapp");
		webapp1.setContextPath("/athena");
		
		// Init global functional features
//		webapp1.addEventListener(new InitApplication());
		
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

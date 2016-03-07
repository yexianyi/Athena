package net.yxy.athena.main;

import java.net.URI;
import java.net.URL;
import java.util.Collections;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import net.yxy.athena.rest.api.ServerService;

public class AppMain {

	public static void main(String[] args) throws Exception {
		// Create a basic jetty server object that will listen on port 8080.
        // Note that if you set this to port 0 then a randomly available port
        // will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
		Server server = new Server(8080);
		
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
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
//        server.setHandler(security);
        
        // First you see the constraint mapping being applied to the handler as
        // a singleton list, however you can passing in as many security
        // constraint mappings as you like so long as they follow the mapping
        // requirements of the servlet api. Next we set a BasicAuthenticator
        // instance which is the object that actually checks the credentials
        // followed by the LoginService which is the store of known users, etc.
        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator()); //BASIC AUTH METHOD
        security.setLoginService(loginService);
        
		// Binds REST Service classes
		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages(ServerService.class.getPackage().getName());
		resourceConfig.register(JacksonFeature.class);
		
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletHolder sh = new ServletHolder(servletContainer);
		
		URL url = AppMain.class.getClassLoader().getResource("html/");
	    URI webRootUri = url.toURI();
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/athena");
		context.setBaseResource(Resource.newResource(webRootUri));
	    context.setWelcomeFiles(new String[] { "index.html" });
		context.addServlet(sh, "/*");
		
		ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
	    holderPwd.setInitParameter("dirAllowed", "true");
	    context.addServlet(holderPwd, "/");

		
		
		
		// Inject Security Handler into Context in order to secure REST Service
		context.setHandler(security);
		server.setHandler(context);
		
		
//		ResourceHandler resource_handler = new ResourceHandler();
//	    resource_handler.setDirectoriesListed(true);
//	    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
//
//	    resource_handler.setResourceBase(".");
//
//	    HandlerList handlers = new HandlerList();
//	    handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
//	    server.setHandler(handlers);
		
		

		try {
			server.start();
			server.join();
		} finally {
//			server.destroy();
		}
	}
}

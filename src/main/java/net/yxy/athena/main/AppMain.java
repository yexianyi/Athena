package net.yxy.athena.main;

import net.yxy.athena.rest.api.ServerService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class AppMain {

	public static void main(String[] args) throws Exception {

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages(ServerService.class.getPackage().getName());
		resourceConfig.register(JacksonFeature.class);
		
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletHolder sh = new ServletHolder(servletContainer);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/athena");
		context.addServlet(sh, "/*");
		
		Server server = new Server(8080);
		server.setHandler(context);

		try {
			server.start();
			server.join();
		} finally {
//			server.destroy();
		}
	}
}

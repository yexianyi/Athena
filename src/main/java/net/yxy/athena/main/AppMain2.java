package net.yxy.athena.main;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class AppMain2 {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		// Handler for multiple web apps
		HandlerCollection handlers = new HandlerCollection();

		// Creating the first web application context
		WebAppContext webapp1 = new WebAppContext();
		webapp1.setResourceBase("src/main/webapp");
		webapp1.setContextPath("/athena");
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

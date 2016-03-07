package net.yxy.athena.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
	 public RestApplication() {
	     // add rest service packages
	     packages("net.yxy.athena.rest.api");
	 }
}

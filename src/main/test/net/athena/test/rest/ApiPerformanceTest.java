package net.athena.test.rest;

import java.util.ArrayList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.representation.Form;

public class ApiPerformanceTest extends Thread{
	
	private final String URL_LOGIN = "http://localhost:8080/athena/j_security_check";
	private ThreadLocal<Client> thLocalClient = new ThreadLocal<Client>() ;
	
	
	@Override
	public void run(){
		login() ;
		
		while(!isInterrupted()){
			System.out.println(sendRequest()) ;
		}
	}
	
	private void login(){
		try {
			// add a filter to set cookies received from the server and to check if login has been triggered
			thLocalClient.set(Client.create());
			Client client = thLocalClient.get();
			client.addFilter(new ClientFilter() {
			    private ArrayList<Object> cookies;

				@Override
				public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
					if (cookies != null) {
			            request.getHeaders().put("Cookie", cookies);
			        }
					ClientResponse response = getNext().handle(request);
			        // copy cookies
			        if (response.getCookies() != null) {
			            if (cookies == null) {
			                cookies = new ArrayList<Object>();
			            }
			            // A simple addAll just for illustration (should probably check for duplicates and expired cookies)
			            cookies.addAll(response.getCookies());
			        }
					
					return response;
				}
			});

			String username = "admin";
			String password = "admin";

			// Login:
			WebResource webResource = client.resource(URL_LOGIN);
			com.sun.jersey.api.representation.Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			webResource.type("application/x-www-form-urlencoded").post(form) ;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		

	}

	private int sendRequest() {
		int status = -1 ;
		try{
			Client client = thLocalClient.get() ;
			WebResource webResource2 = client.resource("http://localhost:8080/athena/api/admin/server/list");
			ClientResponse response = webResource2.accept("application/json").get(ClientResponse.class);
			status = response.getStatus();
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return status;
	}

	public static void main(String[] args) {
		int n=100 ;
		
		for(int i=0; i<n; i++){
			ApiPerformanceTest tester = new ApiPerformanceTest() ;
			tester.start();
		}
		
	}

}

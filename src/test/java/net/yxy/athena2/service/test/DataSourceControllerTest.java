package net.yxy.athena2.service.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.yxy.athena.util.TimeUtil;
import net.yxy.athena2.app.Application;
import net.yxy.athena2.controller.DataSourceController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataSourceControllerTest extends MockMvcResultMatchers{
    
	@LocalServerPort 
	private int port ;
	private MockMvc mvc;

    
    @Before
    public void setup(){
       mvc = MockMvcBuilders.standaloneSetup(new DataSourceController()).build();
    }
    
    @Test
    public void testHandleRequest() throws InterruptedException {
    	
    	int numReq = 500 ;
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        final CountDownLatch latch = new CountDownLatch(numReq);
        
        for(int i=0; i<numReq; i++) {
        	final HttpGet request = new HttpGet("http://localhost:"+port+"/handle?datasource="+i);
//        	final HttpGet request = new HttpGet("http://localhost:8080/handle?datasource="+i);
        	System.out.println("Sent request#"+i+" at "+TimeUtil.getCurrentTime());
        	final int reqId= i ;
        	httpclient.execute(request, new FutureCallback<HttpResponse>() {
        		public void completed(final HttpResponse response) {
        			latch.countDown();
        			System.out.println("Sent request#"+reqId+" received COMPLETED at"+TimeUtil.getCurrentTime());
//                System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
        		}
        		
        		public void failed(final Exception ex) {
        			latch.countDown();
        			System.out.println("Sent request#"+reqId+" received failure at"+TimeUtil.getCurrentTime());
        			ex.printStackTrace();
        		
        		}
        		
        		public void cancelled() {
        			latch.countDown();
        			System.out.println("Sent request#"+reqId+" received cancel at"+TimeUtil.getCurrentTime());
        		}
        		
        	});
        	
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            httpclient.close();
        } catch (IOException ignore) {

        }
        
        Thread.sleep(100000);

    }
	
//	@Test
//	public void testHandleRequest() throws Exception {
//		RequestBuilder request = null;
//		request = MockMvcRequestBuilders.get("/handle");
//		for (int i = 0; i < 10; i++) {
//			mvc.perform(request).andExpect(status().isOk());
//		}
//
//		 Thread.sleep(1000000);
//
//	}
	
}

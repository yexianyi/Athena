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
package net.yxy.athena.global;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Synchronizer extends Thread{
	static private Logger logger = LoggerFactory.getLogger(Synchronizer.class); 
	
	private volatile static Synchronizer instance;
	private volatile static String hashtext;
	private volatile boolean isInterrupted = false ;

	private Synchronizer() {
	}
	

	public static Synchronizer createInstance() {
		if (instance == null) {
			synchronized (Synchronizer.class) {
				if (instance == null){
					instance = new Synchronizer();
				}
			}
		}
		return instance;
	}
	
	
	public static String getCurrent(){
		return hashtext;
	}
	
	public boolean shutdown(){
		isInterrupted = true ;
		return this.isAlive() ;
	}
	
	    
	@Override
	public void run() {
		super.run();
		while(!isInterrupted() && !isInterrupted){
			try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            byte[] bytes = String.valueOf(System.currentTimeMillis()).getBytes() ;
	            byte[] digest = md.digest(bytes);
	            
	            StringBuffer sb = new StringBuffer();
	    		for (byte b : digest) {
	    			sb.append(String.format("%02x", b & 0xff));
	    		}
	            
	    		hashtext = sb.toString().substring(8, 24);  ;
//	    		logger.debug(hashtext);
	    		
	    		Thread.sleep(Constants.REFRESH_INTERVAL_MILISEC);
	        }
	        catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        } catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args) {
		Synchronizer syn = new Synchronizer() ;
		syn.start(); 
		
		Thread th = new Thread(){
			@Override
			public void run() {
				while(true){
					System.out.println(Synchronizer.getCurrent()) ;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		th.start();
		
		

	}

}

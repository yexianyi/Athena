package net.yxy.athena.global;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Synchronizer extends Thread{
	
	private volatile static String hashtext;
	private static final int interval = 3000 ;

	private Synchronizer() {
	}
	
	public static String getCurrent(){
		return hashtext;
	}

	    
	@Override
	public void run() {
		super.run();
		while(!isInterrupted()){
			try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            byte[] bytes = String.valueOf(System.currentTimeMillis()).getBytes() ;
	            byte[] digest = md.digest(bytes);
	            
	            StringBuffer sb = new StringBuffer();
	    		for (byte b : digest) {
	    			sb.append(String.format("%02x", b & 0xff));
	    		}
	            
	    		hashtext = sb.toString().substring(8, 24);  ;
	    		
	    		Thread.sleep(interval);
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

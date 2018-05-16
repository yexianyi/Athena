package net.yxy.athena.util;

import java.util.Random;

public final class MathUtil {
	
	public static int getRandomInt(int from, int to) {
		Random rand = new Random();  
		return rand.nextInt(to) + from ;
	}

	
	public static void main(String[] args) {
		for(int i=0; i<10; i++) {
			System.out.println(MathUtil.getRandomInt(0, 3)) ;
		}
	}
}

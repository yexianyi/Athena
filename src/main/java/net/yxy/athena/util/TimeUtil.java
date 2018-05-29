package net.yxy.athena.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class TimeUtil {

	public static String getCurrentTime() {
		Calendar cal = Calendar.getInstance(); 
		Date date = cal.getTime();
		//  2016/05/05-01:01:34:364
		return (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date)).toString();
	}
	
	
	public static void main(String[] args) {
		System.out.println(TimeUtil.getCurrentTime()) ;
	}
}

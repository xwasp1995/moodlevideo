package com.moodlevideo.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeTest {
	
	public static void main(String[] args) {
		
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();
		System.out.println(timeZone.getID());
		System.out.println(timeZone);
		
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8")); // 设置默认时区为东八区
		long a = System.currentTimeMillis(); // 获取当前时间戳
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z"); // 设置输出格式
		System.out.println(sdf.format(a)); // 将时间戳转化为东八区时间
		
	}

}

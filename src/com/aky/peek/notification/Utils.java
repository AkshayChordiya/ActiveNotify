package com.aky.peek.notification;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class Utils {
	
	
	public static void CopyStream(InputStream is, OutputStream os){
		final int buffer_size=1024;
		try{
			byte[] bytes=new byte[buffer_size];
			for(;;){
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(IndexOutOfBoundsException ex){
			
		}
		catch (IOException e) {
		}
	}
	 
	@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
	public static String TimeConvert(long time){
		
		String[] timeString = {"",""};
		
		try{
			timeString = new SimpleDateFormat("hh:mm").format(new Date(time)).split(":");
		}catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		//int sec = (int)(time / 1000) % 60;
		//int min = (int)((time / (1000 *60)) % 60);
		//int hr = (int)((time/(1000*60*60)) % 24);
		
		/*
		String timer = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), 
				TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
				TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
				*/
		String timer = "";
		try{
			timer = timeString[0] + ":" + timeString[1];
		}
		catch (ArrayIndexOutOfBoundsException e) {
		}
		return timer;
		
	}

}

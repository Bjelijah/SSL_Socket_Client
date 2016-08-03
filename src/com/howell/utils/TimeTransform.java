package com.howell.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.codehaus.jackson.map.util.ISO8601DateFormat;
import org.codehaus.jackson.map.util.ISO8601Utils;

import android.util.Log;

public class TimeTransform {
     public static String utcToTimeZoneDate(long date, TimeZone timeZone, DateFormat format){
         Date dateTemp = new Date(date);
         format.setTimeZone(timeZone);
         return format.format(dateTemp);
     }
     
     public static String dateToString(Date date){
    	 SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         String stringTime = foo.format(date);
         return stringTime;
     }
     
     public static String reduceTenDays(Date date ){
    	 SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
 		 Calendar cal = Calendar.getInstance();
 		 cal.setTime(date);
 		 cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 10);
		 String endDate = dft.format(cal.getTime());
		 return endDate;
     }
     
     public static Date StringToDate(String string){
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	 Date date = null;
		 try {
			date = sdf.parse(string);
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		
    	 return date;
     }
     
     
     public static String Date2ISODate(Date date){	
 		ISO8601DateFormat isoDate = new ISO8601DateFormat();
 		String isoString = isoDate.format(date);
 		Log.i("123", "isoDate:"+isoString);
 		return isoString;
 	}

 	public static String ISODateString2Date(String isoDate){
 		String str = null;
 		try {
 			Date date = ISO8601Utils.parse(isoDate);
 			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 			str = sdf.format(date);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return str;

 		//		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");  
 		//		DateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
 		//		try {  
 		//			return sd.format(sdf.parse(isoDate));  
 		//		} catch (ParseException e) {  
 		//			e.printStackTrace();  
 		//			return null;  
 		//		}  
 	}

 	public static String ISODateString2ISOString(String isoDate){
 		String str = null;
 		try{
 			Date date = ISO8601Utils.parse(isoDate);
 			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
 			str = sdf.format(date);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return str;
 	}  
}



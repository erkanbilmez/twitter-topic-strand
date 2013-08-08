package twittertopicstrand.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
	private Date _date = null;
	private DateFormat df;
	
	public DateConverter () {
	}
	
	public boolean set (String longStr) {
		if (longStr == null)
			return false;
		
		try {
			df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
			_date = df.parse(longStr);
			return true;
		} catch (ParseException e) {
			_date = null;
			return false;
		}
	}
	
	public boolean set(String timeStr, String dateStr) {
		if (timeStr == null)
			return false;
		if (dateStr == null)
			return false;
		
		try {
			String longStr = timeStr + " " + dateStr;
			
			df = new SimpleDateFormat("HH:mm:ss yyyyMMdd");
			_date = df.parse(longStr);
			return true;
		} catch (ParseException e) {
			_date = null;
			return false;
		}
	}
	
	public boolean set(Date date) {
		try {
			_date = date;
			return true;
		} catch (Exception e) {
			_date = null;
			return false;
		}
	}
	
	public Date getDate () {
		return _date;
	}
	
	public String getLongStr () {
		if (_date == null)
			return null;
		
		try {
			df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
			return df.format(_date);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public String [] getSplitStr () {
		if (_date == null)
			return null;
		
		try {
			String result [] = new String [2];
			
			df = new SimpleDateFormat("HH:mm:ss");
			result[0] = df.format(_date);
			
			df = new SimpleDateFormat("yyyyMMdd");
			result[1] = df.format(_date);
			
			return result;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static int ToInt (Date d)
	{
		return ((d.getYear() * 12 + d.getMonth()) * 31 + d.getDay())* 24 + d.getHours();
	}
	
}

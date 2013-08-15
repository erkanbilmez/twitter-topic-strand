package twittertopicstrand.util;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class HourOperations {
	
	public static int getHourId(DateTime start, DateTime dt){		
		Duration d = new Duration(start, dt);
		return (int) d.getMillis() / (1000 * 60 * 60);
	}
	
	public static DateTime getDateTime(String str){
		DateTime rVal;
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd-HH:mm:ss");
		
		rVal= formatter.parseDateTime(str);
		return rVal;
	}
}

package twittertopicstrand.util;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

public class HourOperations {
	
	public static int getHourId(DateTime start, DateTime dt){		
		Duration d = new Duration(start, dt);
		return (int) d.getMillis() / (1000 * 60 * 60);
	}
	
}

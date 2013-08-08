package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import twitter4j.LightStatus;

public class TopicSplitter { 
	
	static int lowThreshold = 3;
	static int highThreshold = 50;	
	
	public static List<LightStatus[]> splitTopics(String hashTag, LightStatus[] statuses) {
		HashMap<Integer,Integer> hours = GetHours(statuses);
		List<Hour> filtered = FilterLows(hours);
		List<DiscussionDuration> groups = Group(filtered, lowThreshold, highThreshold);

		List<List<LightStatus>> rVal = new ArrayList<List<LightStatus>>();		
		for (int i=0; i<groups.size(); i++)
			rVal.add(new ArrayList<LightStatus>());
		
		for (LightStatus ls : statuses)
		{
			int hourId = GetHourId(ls.createdAt);
			for (int i=0; i<groups.size(); i++)
			{
				if (hourId >= groups.get(i).StartHour && hourId <= groups.get(i).EndHour)
					rVal.get(i).add(ls);
			}
		}
		
		hours.clear();		
		filtered.clear();
		groups.clear();
		
		return ConvertToArray(rVal);		
	}
	
	public static List<LightStatus[]> ConvertToArray (List<List<LightStatus>> lists)
	{
		List<LightStatus[]> arraylist = new ArrayList<LightStatus[]>();
		for (List<LightStatus> list : lists)
		{
			LightStatus[] array = (LightStatus[])list.toArray();
			arraylist.add(array);
		}
		return arraylist;
	}
	
	public static int GetHourId (Date d)
	{
		return ((((d.getYear() * 12) + d.getMonth()) * 31) + d.getDate()) * 24 + d.getHours();
	}
	
	public static HashMap<Integer, Integer> GetHours (LightStatus[] statuses)
	{
		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		for (LightStatus ls : statuses)
		{
			int hour = GetHourId( ls.createdAt);
			if (list.containsKey(hour))
				list.put(hour, list.get(hour)+1);
			else
				list.put(hour, 1);
		}
		return list;
	}
	
	public static List<Hour> FilterLows (HashMap<Integer, Integer> hours )
	{
		List<Hour> list = new ArrayList<Hour>();
		
		Integer[] keys = (Integer[])hours.keySet().toArray();
		for (Integer key : keys)
		{
			int value = hours.get(key);
			if (value > lowThreshold)
				list.add(new Hour(key, value));
		}
				
		return list;
	}
	
	public static List<DiscussionDuration> Group (List<Hour> hours, int lowThreshold, int highTreshold)
	{			
		if (hours.size() == 0)
			return null;
		
		boolean hasHigh = false;
		
		for (int i=0; i<hours.size(); i++)
			if (hours.get(i).Count >= highTreshold)
			{
				hasHigh = true;
				break;
			}
		
		if (!hasHigh)
			return null;			
		
		for (int i=hours.size()-1; i>=0; i--)
			if (hours.get(i).Count < lowThreshold)
				hours.remove(i);
		
		if (hours.size() == 0)
			return null;
		
		int lastHourId = -1;
		int currentHourId = 0;
		boolean groupHasHigh = false;
		
		List<List<Hour>> groups = new ArrayList<List<Hour>>();
		List<Hour> group = new ArrayList<Hour>();
		
		for (int i=0; i<hours.size(); i++)
		{
			currentHourId  = hours.get(i).HourId;
			if (lastHourId == -1)
			{
				group.add(hours.get(i));
				lastHourId = currentHourId;
			}
			else if (currentHourId == lastHourId + 1)
			{
				group.add(hours.get(i));
				lastHourId = currentHourId;
			}
			else
			{
				groupHasHigh = false;
				for (int j=0; j<group.size(); j++)
					if (group.get(j).Count >= highTreshold)
					{
						groupHasHigh = true;
						break;
					}
				
				if (groupHasHigh)
					groups.add(group);
				
				group = new ArrayList<Hour>();
				lastHourId = -1;
			}
		}
		
		groups.add(group);
		hours.clear();
		
		List<DiscussionDuration> r = new ArrayList<DiscussionDuration>();
		for (int i=0; i<groups.size(); i++) {
			int size = groups.get(i).size();
			DiscussionDuration disc = new DiscussionDuration();
			for (int j=0; j<size; j++) {
				int hour = groups.get(i).get(j).HourId;
				if (disc.StartHour == -1 || disc.StartHour > hour)
					disc.StartHour = hour;
				if (disc.EndHour == -1 || disc.EndHour < hour)
					disc.EndHour = hour;
			}
		}
		groups.clear();
		
		return r;
	}
	
	public static class DiscussionDuration {
		public int StartHour;
		public int EndHour;
		
		public DiscussionDuration () {
			this.StartHour = -1;
			this.EndHour = -1;
		}
	}
	
	public static class Hour {
		public int HourId;
		public int Count;
		
		public Hour(int h, int c) {
			HourId = h;
			Count = c;
		}
	}
	
	public static class MyComparator implements Comparator<Hour>
	{
		@Override
		public int compare(Hour arg0, Hour arg1) {
			return (arg0.HourId>arg1.HourId ? 1 : (arg0.HourId==arg1.HourId ? 0 : -1));
		}
	}
	
}
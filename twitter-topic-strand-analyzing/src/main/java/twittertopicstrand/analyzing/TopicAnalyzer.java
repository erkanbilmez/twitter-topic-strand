package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

public class TopicAnalyzer {
	
	// Params:
	int lowThreshold = 3;
	int highThreshold = 50;
	

	String hashTag;
	String tweetCount;
	String participantCount;
	DateTime firstDate;
	DateTime lastDate;
	List<UserAnalyzer> analyzers;
	
	List<LightStatus> statuses;
	      
	public TopicAnalyzer(String hashtag, List<LightStatus> statuses) throws IOException {
		
		this.statuses = statuses;
		this.hashTag = hashtag;
				
		List<Hour> hours = GetHourList(statuses);
		List<List<Hour>> hourGroups = Group(hours, lowThreshold, highThreshold);
		List<GroupDisc> discGroups = new ArrayList<GroupDisc>();
		
		for (int i=0; i<hourGroups.size(); i++)
			discGroups.add(new GroupDisc(hourGroups.get(i)));
		
		List<List<LightStatus>> statusGroups = new ArrayList<List<LightStatus>>();
		
		for (int i=0; i<discGroups.size(); i++)
			statusGroups.add(new ArrayList<LightStatus>());
		
		int hourId = 0;
		for (LightStatus m : statuses) {
			hourId = UserAnalyzer.GetHourId(m.createdAt);
			for (int i=0; i<discGroups.size(); i++) {
				if (hourId >= discGroups.get(i).StartHour && hourId <= discGroups.get(i).EndHour) {
					statusGroups.get(i).add(m);
					break;
				}
			}
		}
		
		hours.clear();
		hourGroups.clear();
		discGroups.clear();
		
		this.analyzers =  new ArrayList<UserAnalyzer>();
		for (int i=0; i<statusGroups.size(); i++) {
			UserAnalyzer analyzer = new UserAnalyzer(this.hashTag);
			analyzer.Load(statusGroups.get(i));
			analyzer.AnalyzeHeroes();
			analyzer.AnalyzeVeterans();
			this.analyzers.add(analyzer);
		}
	}
	
	public List<Hour> GetHourList (List<LightStatus> statuses)	{		
		List<Hour> hours = new ArrayList<Hour>();
		
		int hourIndex = -1;
		int hourId = 0;
		for (int i=0; i<statuses.size(); i++)
		{
			hourIndex = -1;
			hourId = UserAnalyzer.GetHourId(statuses.get(i).createdAt);
			for (int j=0; j<hours.size(); j++)
				if (hours.get(j).HourId == hourId)
				{
					hourIndex = j;
					break;
				}
			
			if (hourIndex == -1)
				hours.add(new Hour(hourId, 1));
			else
				hours.get(hourIndex).Count++;
		}
		return hours;
	}
	
	public List<List<Hour>> Group (List<Hour> hours, int lowThreshold, int highTreshold) 	{			
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
		
		Collections.sort(hours, new MyComparator());
		
		int lastHourId = -1;
		int currentHourId = 0;
		boolean groupHasHigh = false;
		
		List<List<Hour>> result = new ArrayList<List<Hour>>();
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
					result.add(group);
				
				group = new ArrayList<Hour>();
				lastHourId = -1;
			}
		}
		
		result.add(group);
		hours.clear();
		
		return result;
	}
	
	public void Load (List<LightStatus> mlist)	{
		int lowThreshold = 3;
		int highThreshold = 50;
		
		List<Hour> hours = new ArrayList<Hour>();
		
		int hourIndex = -1;
		int hourId = 0;
		for (int i=0; i<mlist.size(); i++)
		{
			hourIndex = -1;
			hourId = UserAnalyzer.GetHourId(mlist.get(i).createdAt);
			for (int j=0; j<hours.size(); j++)
				if (hours.get(j).HourId == hourId)
				{
					hourIndex = j;
					break;
				}
			
			if (hourIndex == -1)
				hours.add(new Hour(hourId, 1));
			else
				hours.get(hourIndex).Count++;
		}
		
		List<List<Hour>> groups = Group(hours, lowThreshold, highThreshold);
		if (groups == null)
			return;
	}
	
	public List<JSONObject> toJSONObject() {
		List<JSONObject> hashtagJsons = new ArrayList<JSONObject>();
		
		for (UserAnalyzer analyzer : analyzers)
		{
			JSONObject rVal = new JSONObject();
			
			try {
				rVal.append("Hashtag", analyzer.Hashtag);
				rVal.append("TweetCount", analyzer.TweetCount);
				rVal.append("ParticipantCount", analyzer.GetParticipantCount());
				rVal.append("VeteranCount", analyzer.GetVeteranCount());
				rVal.append("HeroCount", analyzer.GetHeroCount());
				rVal.append("FirstHour", analyzer.FirstHour);
				rVal.append("LastHour", analyzer.LastHour);
				rVal.append("Sequence", analyzer.Duration());
				
				List<TimeNode> volumeTimeNodes = new ArrayList<TimeNode>();
				
				for (int i=analyzer.FirstHour; i<=analyzer.LastHour; i++)
				{
					TimeNode t = new TimeNode();
					t.HourId = i;
					volumeTimeNodes.add(t);
				}
				
				for (TimeNode t : volumeTimeNodes)
				{
					t.TweetCount = analyzer.GetTweetCount(t.HourId);
					t.ParticipantCount = analyzer.GetParticipantCount(t.HourId);
					t.VeteranCount = analyzer.GetVeteranCount(t.HourId);
					t.HeroCount = analyzer.GetHeroCount(t.HourId);
				}
				
				//Create Volume lists
				
				JSONArray volume_tweet = new JSONArray();
				JSONArray volume_participant = new JSONArray();
				JSONArray volume_veteran = new JSONArray();
				JSONArray volume_hero = new JSONArray();
				
				for (TimeNode t : volumeTimeNodes)
				{
					volume_tweet.put(t.TweetCount);
					volume_participant.put(t.ParticipantCount);
					volume_veteran.put(t.ParticipantCount);
					volume_hero.put(t.ParticipantCount);
				}
				
				rVal.append("TweetVolume", volume_tweet);
				rVal.append("ParticipantVolume", volume_participant);
				rVal.append("VeteranVolume", volume_veteran);
				rVal.append("HeroVolume", volume_hero);
				
				//Create Summary Lists
				
				List<TimeNode> summaryTimeNodes = Summarize(volumeTimeNodes);
				
				JSONArray summary_tweet = new JSONArray();
				JSONArray summary_participant = new JSONArray();
				JSONArray summary_veteran = new JSONArray();
				JSONArray summary_hero = new JSONArray();
				
				for (TimeNode t : summaryTimeNodes)
				{
					summary_tweet.put(t.TweetCount);
					summary_participant.put(t.ParticipantCount);
					summary_veteran.put(t.ParticipantCount);
					summary_hero.put(t.ParticipantCount);
				}
	
				rVal.append("TweetSummary", summary_tweet);
				rVal.append("ParticipantSummary", summary_participant);
				rVal.append("VeteranSummary", summary_veteran);
				rVal.append("HeroSummary", summary_hero);
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			hashtagJsons.add(rVal);
		}

		return hashtagJsons;
		
//		Example code:
		
//		JsonObject innerObject = new JsonObject();
//		innerObject.addProperty("name", "john");
//
//		JsonObject jsonObject = new JsonObject();
//		jsonObject.add("publishr", innerObject);
//      {"publisher":{"name":"john"}}
		
//		Output format:
		
//		{
//			"HashTag":"direndersim", "TweetCount":14012, "ParticipantCount":9602, "VeteranCount":0, "HeroCount":3, "MissionaryCount":0, "FirstDate":"Sun Jun 02 23:03:00 VET 2013", "LastDate":"Tue Jun 25 13:07:19 VET 2013", "FirstHour":"2013.6.3.23", "LastHour":"2013.6.26.13", "SequenceCount":543,
//
//			"TweetVolume" : [0,0,1,2,3,0,2,1],
//			"ParticipantVolume" : [3,2,1,2,2,0,2,1],
//			"HeroVolume" : [3,2,1,2,2,0,2,1], 	
//			"VeteranVolume" : [3,2,1,2,2,0,2,1], 	
//			
//			"TweetSummary" : [0,0,1,2,3,0,2,1],
//			"ParticipantSummary" : [3,2,1,2,2,0,2,1],
//			"HeroSummary" : [3,2,1,2,2,0,2,1], 	
//			"VeteranSummary" : [3,2,1,2,2,0,2,1]
//		}
	}
	
	public List<TimeNode> Summarize (List<TimeNode> nodes)	{
		List<TimeNode> summary = new ArrayList<TimeNode>();
		
		if (nodes.size() == 0)
		{	
			for (int i=0; i<10; i++)
				summary.add(new TimeNode());
			return summary;
		}
		
		int size = nodes.size();
		double part = (double) size / 10.0;
		
		for (int i=0; i<10; i++)
		{
			int pos = (int) part * i;
			if (pos < size)
				pos = size;
			summary.add(nodes.get(pos));
		}
		
		return summary;
	}
	
	public class TimeNode {
		public int HourId;
		public int TweetCount;
		public int ParticipantCount;
		public int VeteranCount;
		public int HeroCount;
	}
	
	public class Hour {
		public int HourId;
		public int Count;
		
		public Hour (int h, int c)
		{
			HourId = h;
			Count = c;
		}
	}
	
	public static class MyComparator implements Comparator<Hour> {
		@Override
		public int compare(Hour arg0, Hour arg1) {
			return (arg0.HourId > arg1.HourId ? 1 : (arg0.HourId == arg1.HourId ? 0 : -1));
		}
	}
	
	public class GroupDisc 	{
		public int StartHour;
		public int EndHour;
		
		public GroupDisc (List<Hour> hour)
		{
			StartHour = 0;
			EndHour = 0;
			for (Hour h : hour)
			{
				if (StartHour == 0 || StartHour > h.HourId)
					StartHour = h.HourId;
				if (EndHour == 0 || EndHour < h.HourId)
					EndHour = h.HourId;
			}
		}
	}
}

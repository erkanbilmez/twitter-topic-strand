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

	String topicIdentifier;
	String tweetCount;
	String participantCount;
	
	DateTime firstDate;
	DateTime lastDate;
	
	UserAnalyzer analyzer;
	LightStatus[] statuses;
	      
	public TopicAnalyzer(String topicIdentifier, LightStatus[] statuses) throws IOException {
		
		this.topicIdentifier = topicIdentifier;
		this.statuses = statuses;
		
		this.analyzer = new UserAnalyzer( this.topicIdentifier, statuses );
		
		analyzer.AnalyzeHeroes();
		analyzer.AnalyzeVeterans();
	}
	
	public JSONObject toJSONObject() throws JSONException {		
		
		JSONObject rVal = new JSONObject();
		
		rVal.put("Hashtag", analyzer.Hashtag);
		rVal.put("TweetCount", analyzer.TweetCount);
		rVal.put("ParticipantCount", analyzer.GetParticipantCount());
		rVal.put("VeteranCount", analyzer.GetVeteranCount());
		rVal.put("HeroCount", analyzer.GetHeroCount());
		rVal.put("FirstHour", analyzer.FirstHour);
		rVal.put("LastHour", analyzer.LastHour);
		rVal.put("Sequence", analyzer.Duration());
		
		List<TimeNode> volumeTimeNodes = new ArrayList<TimeNode>();
		
		for (int i=analyzer.FirstHour; i<=analyzer.LastHour; i++) {
			TimeNode t = new TimeNode();
			t.HourId = i;
			volumeTimeNodes.add(t);
		}
		
		for (TimeNode t : volumeTimeNodes) {
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
		
		for (TimeNode t : volumeTimeNodes) {
			volume_tweet.put(t.TweetCount);
			volume_participant.put(t.ParticipantCount);
			volume_veteran.put(t.ParticipantCount);
			volume_hero.put(t.ParticipantCount);
		}
		
		rVal.put("TweetVolume", volume_tweet);
		rVal.put("ParticipantVolume", volume_participant);
		rVal.put("VeteranVolume", volume_veteran);
		rVal.put("HeroVolume", volume_hero);
		
		//Create Summary Lists
		
		List<TimeNode> summaryTimeNodes = Summarize(volumeTimeNodes);
		
		JSONArray summary_tweet = new JSONArray();
		JSONArray summary_participant = new JSONArray();
		JSONArray summary_veteran = new JSONArray();
		JSONArray summary_hero = new JSONArray();
		
		for (TimeNode t : summaryTimeNodes)	{
			summary_tweet.put(t.TweetCount);
			summary_participant.put(t.ParticipantCount);
			summary_veteran.put(t.ParticipantCount);
			summary_hero.put(t.ParticipantCount);
		}

		rVal.put("TweetSummary", summary_tweet);
		rVal.put("ParticipantSummary", summary_participant);
		rVal.put("VeteranSummary", summary_veteran);
		rVal.put("HeroSummary", summary_hero);

		return rVal;
		
		// Example code
		
//		JSONObject obj = new JSONObject();
//    	obj.put("publisher", "hebe");
//    	
//    	JSONObject inner = new JSONObject();
//    	inner.put("publisher", "sait");
//    	
//    	obj.put("a", inner);
//    	
//    	JSONArray arr = new JSONArray();
//    	arr.put("a");
//    	arr.put("b");
//    	arr.put("c");
//    	
//    	obj.put("array", arr);
				
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

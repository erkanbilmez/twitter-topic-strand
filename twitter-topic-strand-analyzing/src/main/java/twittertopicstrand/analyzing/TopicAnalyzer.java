package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.util.HourOperations;

public class TopicAnalyzer {

	String topicIdentifier;
	LightStatus[] statuses;
	
	DateTime firstTime;
	DateTime lastTime;
	
	HashSet<Long> allParticipants = new HashSet<Long>();
		      
	VeteranAnalyzer veteranAnalyzer;
	HeroAnalyzer heroAnalyzer;
	
	public TopicAnalyzer(String topicIdentifier, LightStatus[] statuses) throws IOException {
		this.topicIdentifier = topicIdentifier;
		this.statuses = statuses;
		
		this.firstTime = new DateTime ( statuses[0].createdAt );
		this.lastTime = new DateTime ( statuses[statuses.length - 1].createdAt );
		
		this.init();
	}
	
	private void init(){
		int length = HourOperations.getHourId(this.firstTime, this.lastTime) + 1;
		
		ArrayList < HashMap<Long, Integer> > participants = new ArrayList< HashMap<Long, Integer> > (length);
		
		for(int i=0;i<length;i++) {
			participants.add( new HashMap<Long, Integer>() );
		}
			
		for(int i=0;i<statuses.length;i++) {
			long userId = statuses[i].userId;
			int hourId = HourOperations.getHourId(this.firstTime, new DateTime ( statuses[i].createdAt ));
		
			int count = participants.get(hourId).containsKey(userId) ? participants.get(hourId).get(userId) : 0;
			participants.get(hourId).put(userId, count + 1);
		}
		
		this.veteranAnalyzer.analyze(participants, allParticipants);
		this.heroAnalyzer.analyze(participants, allParticipants, this.statuses);
	}
	
	public JSONObject toJSONObject() throws JSONException {		
		
		JSONObject rVal = new JSONObject();
		
		rVal.put("Hashtag", this.topicIdentifier);
		rVal.put("TweetCount", this.statuses.length);
	
		rVal.put("ParticipantCount", this.allParticipants.size());
		
		rVal.put("VeteranCount", this.veteranAnalyzer.veteranCount);
		//rVal.put("HeroCount", this.heroAnalyzer.heroCount);
		
		rVal.put("FirstHour", this.firstTime);
		rVal.put("LastHour", this.lastTime);
		
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
}

package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	
	ArrayList <HashMap<Long, Integer> > participants;
	HashSet<Long> allParticipants = new HashSet<Long>();
		      
	VeteranAnalyzer veteranAnalyzer = new VeteranAnalyzer();
	HeroAnalyzer heroAnalyzer = new HeroAnalyzer();
	
	public TopicAnalyzer(String topicIdentifier, LightStatus[] statuses) throws IOException {
		this.topicIdentifier = topicIdentifier;
		this.statuses = statuses;
		
		this.firstTime = new DateTime ( statuses[0].createdAt );
		this.lastTime = new DateTime ( statuses[statuses.length - 1].createdAt );
		
		this.init();
	}
	
	private void init(){
		int length = HourOperations.getHourId(this.firstTime, this.lastTime) + 1;
		participants = new ArrayList< HashMap<Long, Integer> > (length);
		
		for(int i=0;i<length;i++) {
			participants.add( new HashMap<Long, Integer>() );
		}
			
		for(int i=0;i<statuses.length;i++) {
			long userId = statuses[i].userId;
			int hourId = HourOperations.getHourId(this.firstTime, new DateTime ( statuses[i].createdAt ));
			
			int count = participants.get(hourId).containsKey(userId) ? participants.get(hourId).get(userId) : 0;
			participants.get(hourId).put(userId, count + 1);
			
			if(!allParticipants.contains(userId)){
				allParticipants.add(userId);
			}
		}
		
		this.veteranAnalyzer.analyze(participants, allParticipants);
		this.heroAnalyzer.analyze(participants, allParticipants, this.statuses);
	}
	
	private int[] getTweetVolumes(){
		int[] rVal = new int[this.participants.size()];
		
		for(int i=0;i<participants.size();i++){
			int total = 0;
			HashMap<Long, Integer> hour = participants.get(i);
			for(Map.Entry<Long, Integer> entry: hour.entrySet()){
				total += entry.getValue();
			}
			rVal[i] = total;
		}
		
		return rVal;
	}
	
	private int[] getParticipantVolumes() {
		int[] rVal = new int[this.participants.size()];
		
		for(int i=0;i<participants.size();i++){
			rVal[i] = participants.get(i).size(); 
		}
		
		return rVal;
	}
	
	private int[] getSummary(int[] arr){
		int[] rVal;
		
		int numItems = 10;
		int itemsPerChunk = arr.length / 10;
		
		rVal = new int[numItems];
				
		for(int i=0;i<rVal.length;i++) {
			int total = 0;
			for(int j=itemsPerChunk * i;j<itemsPerChunk * (i+1);j++){
				total += arr[j]; 
			}
			rVal[i] = total;
		}
		
		return rVal;
	}
	
	public JSONObject toJSONObject() throws JSONException {		
		
		JSONObject rVal = new JSONObject();
		
		rVal.put("Hashtag", this.topicIdentifier);
		rVal.put("TweetCount", this.statuses.length);
		rVal.put("ParticipantCount", this.allParticipants.size() );
		rVal.put("VeteranCount", this.veteranAnalyzer.veteranCount );
		rVal.put("HeroCount", this.heroAnalyzer.heroCount );
		rVal.put("FirstHour", this.firstTime.toString("yyyy-MM-dd-HH:mm:ss"));
		rVal.put("LastHour", this.lastTime.toString("yyyy-MM-dd-HH:mm:ss"));
		
		int[] tweetVolume = getTweetVolumes();
		int[] tweetVolumeSummary = getSummary(tweetVolume);
		
		int[] participantVolume = getParticipantVolumes();
		int[] participantSummary = getSummary(participantVolume);
		
		int[] veteranSummary = getSummary(veteranAnalyzer.veteranCountsByHour);
		int[] heroSummary = getSummary(heroAnalyzer.heroCountsByHour);
		
		rVal.put("TweetVolume", tweetVolume);
		rVal.put("ParticipantVolume", participantVolume);
		rVal.put("VeteranVolume", veteranAnalyzer.veteranCountsByHour);
		rVal.put("HeroVolume", heroAnalyzer.heroCountsByHour);
		
		rVal.put("TweetSummary", tweetVolumeSummary);
		rVal.put("ParticipantSummary", participantSummary);
		rVal.put("VeteranSummary", veteranSummary);
		rVal.put("HeroSummary", heroSummary);
		
		return rVal;
	}
}

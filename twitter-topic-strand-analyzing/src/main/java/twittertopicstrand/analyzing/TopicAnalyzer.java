package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.util.HourOperations;

public class TopicAnalyzer {

	JSONObject mainJson = new JSONObject();
	JSONObject indexJson = new JSONObject();
	
	String topicIdentifier;
	LightStatus[] statuses;
	
	DateTime firstTime;
	DateTime lastTime;
	
	ArrayList <HashMap<Long, Integer> > hourlyParticipants;
	HashSet<Long> allParticipants = new HashSet<Long>();
		      
	VeteranAnalyzer veteranAnalyzer = new VeteranAnalyzer();
	HeroAnalyzer heroAnalyzer = new HeroAnalyzer();
	RetweetAnalyzer retweetAnalyzer = new RetweetAnalyzer();
	
	public TopicAnalyzer(String topicIdentifier, LightStatus[] statuses) throws Throwable {
		this.topicIdentifier = topicIdentifier;
		this.statuses = statuses;
		
		this.firstTime = new DateTime ( statuses[0].createdAt );
		this.lastTime = new DateTime ( statuses[statuses.length - 1].createdAt );
		
		this.init();
	}
	
	private void init() throws Exception {
		
		int length = HourOperations.getHourId(this.firstTime, this.lastTime) + 1;
		hourlyParticipants = new ArrayList< HashMap<Long, Integer> > (length);
		
		for(int i=0;i<length;i++) {
			hourlyParticipants.add( new HashMap<Long, Integer>() );
		}
			
		for(int i=0;i<statuses.length;i++) {
			long userId = statuses[i].userId;
			int hourId = HourOperations.getHourId(this.firstTime, new DateTime ( statuses[i].createdAt ));
			
			int count = hourlyParticipants.get(hourId).containsKey(userId) ? 
									hourlyParticipants.get(hourId).get(userId) : 0;
			hourlyParticipants.get(hourId).put(userId, count + 1);
			
			if(!allParticipants.contains(userId)){
				allParticipants.add(userId);
			}
		}
		
		this.veteranAnalyzer.analyze(hourlyParticipants, allParticipants);
		this.heroAnalyzer.analyze(hourlyParticipants, allParticipants, this.statuses);
		this.retweetAnalyzer.analyze(this.statuses);
		
		this.initJSONObject();
	}
	
	private int[] getTweetVolumes(){
		int[] rVal = new int[this.hourlyParticipants.size()];
		
		for(int i=0;i<hourlyParticipants.size();i++){
			int total = 0;
			HashMap<Long, Integer> hour = hourlyParticipants.get(i);
			for(Map.Entry<Long, Integer> entry: hour.entrySet()){
				total += entry.getValue();
			}
			rVal[i] = total;
		}
		
		return rVal;
	}
	
	private int[] getParticipantVolumes() {
		int[] rVal = new int[this.hourlyParticipants.size()];
		
		for(int i=0;i<hourlyParticipants.size();i++){
			rVal[i] = hourlyParticipants.get(i).size(); 
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
	
	private void initJSONObject() throws JSONException{

		// mainJson
		
		mainJson.put("Hashtag", this.topicIdentifier);
		mainJson.put("TweetCount", this.statuses.length);
		mainJson.put("ParticipantCount", this.allParticipants.size() );
		mainJson.put("VeteranCount", this.veteranAnalyzer.veteranCount );
		mainJson.put("HeroCount", this.heroAnalyzer.heroCount );
		mainJson.put("FirstHour", this.firstTime.toString("yyyy-MM-dd-HH:mm:ss"));
		mainJson.put("LastHour", this.lastTime.toString("yyyy-MM-dd-HH:mm:ss"));
		
		mainJson.put("Veterans", this.veteranAnalyzer.veterans);
		mainJson.put("Heroes", this.heroAnalyzer.heroes);
		
		int[] tweetVolume = getTweetVolumes();
		int[] tweetVolumeSummary = getSummary(tweetVolume);
		
		int[] participantVolume = getParticipantVolumes();
		int[] participantSummary = getSummary(participantVolume);
		
		int[] veteranSummary = getSummary(veteranAnalyzer.veteranCountsByHour);
		int[] heroSummary = getSummary(heroAnalyzer.heroCountsByHour);
		
		mainJson.put("TweetVolume", tweetVolume);
		mainJson.put("ParticipantVolume", participantVolume);
		mainJson.put("VeteranVolume", veteranAnalyzer.veteranCountsByHour);
		mainJson.put("HeroVolume", heroAnalyzer.heroCountsByHour);
		
		mainJson.put("TweetSummary", tweetVolumeSummary);
		mainJson.put("ParticipantSummary", participantSummary);
		mainJson.put("VeteranSummary", veteranSummary);
		mainJson.put("HeroSummary", heroSummary);
		
		mainJson.put("MostRetweetedTweets", this.retweetAnalyzer.mostRetweetedLightStatuses);
		
		// indexJson
		
		indexJson.put("Hashtag", this.topicIdentifier);
		indexJson.put("TweetCount", this.statuses.length);
		indexJson.put("ParticipantCount", this.allParticipants.size() );
		indexJson.put("VeteranCount", this.veteranAnalyzer.veteranCount );
		indexJson.put("HeroCount", this.heroAnalyzer.heroCount );
		indexJson.put("FirstHour", this.firstTime.toString("yyyy-MM-dd-HH:mm:ss"));
		indexJson.put("LastHour", this.lastTime.toString("yyyy-MM-dd-HH:mm:ss"));
		indexJson.put("TweetSummary", tweetVolumeSummary);
		indexJson.put("ParticipantSummary", participantSummary);
	}
	
	public JSONObject getMainJson() {		
		return this.mainJson;
	}
	
	public JSONObject getIndexJSon() {
		return this.indexJson;
	}
}

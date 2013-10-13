package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
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
	NoisyAnalyzer noisyAnalyzer = new NoisyAnalyzer();
	MissionaryAnalyzer missionaryAnalyzer = new MissionaryAnalyzer();
	ApostleAnalyzer apostleAnalyzer = new ApostleAnalyzer();
	RetweetAnalyzer retweetAnalyzer = new RetweetAnalyzer();
	
	JSONArray userTweetCounts = new JSONArray();
	
	public TopicAnalyzer(String topicIdentifier, LightStatus[] statuses) throws Throwable {
		this.topicIdentifier = topicIdentifier;
		this.statuses = statuses;
		
		this.firstTime = new DateTime ( statuses[0].createdAt );
		this.lastTime = new DateTime ( statuses[statuses.length - 1].createdAt );
		
		this.init();
	}
	
	private void init() throws Exception {
		
		allParticipants = new HashSet<Long>();
		
		int length = HourOperations.getHourId(this.firstTime, this.lastTime) + 1;
		hourlyParticipants = new ArrayList< HashMap<Long, Integer> > (length);
		
		for(int i=0;i<length;i++) {
			hourlyParticipants.add( new HashMap<Long, Integer>() );
		}
		
		HashMap<Long, int[]> tweetNumbers = new HashMap<Long, int[]>();
			
		for(int i=0;i<statuses.length;i++) {
			long userId = statuses[i].userId;
			int hourId = HourOperations.getHourId(this.firstTime, new DateTime ( statuses[i].createdAt ));
			
			int count = hourlyParticipants.get(hourId).containsKey(userId) ? 
									hourlyParticipants.get(hourId).get(userId) : 0;
			hourlyParticipants.get(hourId).put(userId, count + 1);
			
			if(!allParticipants.contains(userId)){
				allParticipants.add(userId);
				tweetNumbers.put(userId, new int[length]);				
			}
			
			tweetNumbers.get(userId)[hourId]++;
		}
		
		this.veteranAnalyzer.analyze(hourlyParticipants, allParticipants);
		this.noisyAnalyzer.analyze(hourlyParticipants, allParticipants, this.statuses);
		this.missionaryAnalyzer.analyze(hourlyParticipants, allParticipants, this.statuses);
		//this.apostleAnalyzer.analyze(hourlyParticipants, allParticipants, this.statuses);
		this.retweetAnalyzer.analyze(this.statuses);
		
		for(Entry<Long, int[]> entry: tweetNumbers.entrySet()) {
			Long userId = entry.getKey();
			if(this.veteranAnalyzer.veterans.contains(userId) || 
					this.noisyAnalyzer.noisies.contains(userId) ||
						this.missionaryAnalyzer.missionaries.contains(userId) )  {
				JSONObject temp = new JSONObject();
				temp.put(String.valueOf(entry.getKey()), getSummary( entry.getValue() ) );
				userTweetCounts.put(temp);
			}
		}
		
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
		mainJson.put("NoisyCount", this.noisyAnalyzer.noisyCount );
		mainJson.put("MissionaryCount", this.missionaryAnalyzer.missionaryCount );
		mainJson.put("FirstHour", this.firstTime.toString("yyyy-MM-dd-HH:mm:ss"));
		mainJson.put("LastHour", this.lastTime.toString("yyyy-MM-dd-HH:mm:ss"));
		
		mainJson.put("Veterans", this.veteranAnalyzer.veterans);
		mainJson.put("Noisies", this.noisyAnalyzer.noisies);
		mainJson.put("Missionaries", this.missionaryAnalyzer.missionaries);
		
		int[] tweetVolume = getTweetVolumes();
		int[] tweetVolumeSummary = getSummary(tweetVolume);
		
		int[] participantVolume = getParticipantVolumes();
		int[] participantSummary = getSummary(participantVolume);
		
		int[] veteranSummary = getSummary(veteranAnalyzer.veteranCountsByHour);
		int[] noisySummary = getSummary(noisyAnalyzer.noisyCountsByHour);
		int[] missionarySummary = getSummary(missionaryAnalyzer.missionaryCountsByHour);
		
		mainJson.put("TweetVolume", tweetVolume);
		mainJson.put("ParticipantVolume", participantVolume);
		mainJson.put("VeteranVolume", veteranAnalyzer.veteranCountsByHour);
		mainJson.put("NoisyVolume", noisyAnalyzer.noisyCountsByHour);
		mainJson.put("MissionaryVolume", missionaryAnalyzer.missionaryCountsByHour);
		
		mainJson.put("TweetSummary", tweetVolumeSummary);
		mainJson.put("ParticipantSummary", participantSummary);
		mainJson.put("VeteranSummary", veteranSummary);
		mainJson.put("NoisySummary", noisySummary);
		mainJson.put("MissionarySummary", missionarySummary);
		
		mainJson.put("MostRetweetedTweets", this.retweetAnalyzer.mostRetweetedLightStatuses);
		
		mainJson.put("UserTweetCounts", this.userTweetCounts);
		
		// indexJson
		
		indexJson.put("Hashtag", this.topicIdentifier);
		indexJson.put("TweetCount", this.statuses.length);
		indexJson.put("ParticipantCount", this.allParticipants.size() );
		indexJson.put("VeteranCount", this.veteranAnalyzer.veteranCount );
		indexJson.put("NoisyCount", this.noisyAnalyzer.noisyCount );
		indexJson.put("MissionaryCount", this.missionaryAnalyzer.missionaryCount );
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

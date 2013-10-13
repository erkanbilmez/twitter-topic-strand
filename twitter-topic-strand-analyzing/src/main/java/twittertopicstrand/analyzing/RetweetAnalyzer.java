package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.util.MapOperations;

public class RetweetAnalyzer {

	public JSONArray mostRetweetedLightStatuses;
	
	private static int n = 3;
	
	public void analyze(LightStatus[] statuses) throws JSONException {
		
		this.mostRetweetedLightStatuses = new JSONArray(); 
		
		HashMap<Long, Integer> retweetCounts = new HashMap<Long, Integer>();
		HashMap<Long, JSONObject> retweetInfos = new HashMap<Long, JSONObject>();
		
		for(int i=0;i<statuses.length;i++) {
			LightStatus current = statuses[i];
			
			if(current.retweetedStatusId != -1) {
				int count = retweetCounts.containsKey(current.retweetedStatusId) ? 
						retweetCounts.get(current.retweetedStatusId) : 0;
						
				if(count < 1){
					JSONObject tweet = new JSONObject();
			    	tweet.put("text", current.text); 
			    	tweet.put("id", current.retweetedStatusId);
			    	tweet.put("userId", current.retweetedStatusUserId);
			    	
			    	retweetInfos.put(current.retweetedStatusId, tweet);
				} 
				
				retweetCounts.put(current.retweetedStatusId, count+1);
			}
		}
		
		Map<Long, Integer> sortedMap = MapOperations.sortMapByValue(retweetCounts);
		int i = 0;
		
		for (Map.Entry<Long, Integer> entry : sortedMap.entrySet()) {
			Long temp = entry.getKey();
			
			mostRetweetedLightStatuses.put(retweetInfos.get(temp));
			
			i++;
			
			if(i+1 > n) {
				break;
			}
		}	
	}
}

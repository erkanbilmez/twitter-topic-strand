package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twittertopicstrand.util.MapOperations;

public class RetweetAnalyzer {

	public JSONArray mostRetweetedLightStatuses;
	
	private static int n = 3;
	
	public void analyze(LightStatus[] statuses) {
		
		List<Long> rValList = new ArrayList<Long>();
		
		HashMap<Long, Integer> retweetCounts = new HashMap<Long, Integer>();
		
		for(int i=0;i<statuses.length;i++) {
			LightStatus current = statuses[i];
			
			if(current.retweetedStatusId != -1) {
				int count = retweetCounts.containsKey(current) ? 
						retweetCounts.get(current) : 0;
						
				retweetCounts.put(current.id, count);
			}
		}
		
		Map<Long, Integer> sortedMap = MapOperations.sortMapByValue(retweetCounts);
		int i = 0;
		
		for (Map.Entry<Long, Integer> entry : sortedMap.entrySet()) {
			Long temp = entry.getKey();
			rValList.add(temp);
			i++;
			
			if(i+1 > n) {
				break;
			}
		}

		
		
	}
}

package twittertopicstrand.analyzing;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.MapOperations;

public class DataAnalyzer{
	
  	static JSONObject finalJSON = new JSONObject();
	
	public static Map<String, Integer> createHashMap(String folderPath) throws IOException {
		
		LightStatusSource statusSource = new LightStatusSource(folderPath);
		Map<String, Integer> rVal = new HashMap<String, Integer>();		
		
		do{
			LightStatus[] chunk = statusSource.getChunk();
			
			for(int i=0;i<chunk.length;i++) {
				for(int j=0;j<chunk[i].hashTags.length;j++){
					String currentHashTag = chunk[i].hashTags[j].toLowerCase();
					
					if(!rVal.containsKey(currentHashTag)) {						
						rVal.put(currentHashTag, 1);
					}else{
						rVal.put(currentHashTag, rVal.get(currentHashTag) + 1);
					}		
				}				
			}
			
		}while(statusSource.iterate());
		
		return rVal;
	}
	
	public static String[] getMostTweetedNTopics(String folderPath, int n) throws IOException {
		String[] rVal = null;
		
		List<String> rValList = new ArrayList<String>();
		
		Map myHashMap = createHashMap(folderPath);
		Map<String, Integer> sortedMap = MapOperations.sortMapByValue(myHashMap);
		
		int i = 0;
		
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			String temp = entry.getKey();
			rValList.add(temp);
			i++;
			
			if(i > n) {
				break;
			}
		}
		
		rVal = rValList.toArray(new String[rValList.size()]);
		
		return rVal;		
	}
	
	public static void analyze(String folderPath) throws IOException, JSONException {
		
		int mostNTopics = 10;
		
		String[] topics = getMostTweetedNTopics(folderPath, mostNTopics);
		Map<String, List<LightStatus>> myMap = new HashMap<String, List<LightStatus>>();		
		
		for(int i=0;i<topics.length;i++) {
			myMap.put(topics[i], new ArrayList<LightStatus>());
		}
		
		LightStatusSource statusSource = new LightStatusSource(folderPath);
		
		int numTweets = 0;
		do{
			LightStatus[] chunk = statusSource.getChunk();
			
			for(int i=0;i<chunk.length;i++) {
				for(int j=0;j<chunk[i].hashTags.length;j++){
					
					LightStatus currentStatus = chunk[i];
					String currentHashTag = currentStatus.hashTags[j].toLowerCase();
					
					if(myMap.containsKey(currentHashTag)){						
						myMap.get(currentHashTag).add(currentStatus);
					}					
				}		
				numTweets++;
			}
			
		}while(statusSource.iterate());
						
		for(Map.Entry<String, List<LightStatus>> entry : myMap.entrySet()){
			
			TopicAnalyzer analyzer = new TopicAnalyzer(entry.getKey(), entry.getValue());
			
			finalJSON.put(entry.getKey(), analyzer.toJSONObject());	
		}		
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String finalOutput = gson.toJson( finalJSON );
		
		FileOperations.writeFile(finalOutput, "output.txt");
	}

}
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
		
	public static void analyze(String folderPath) throws IOException, JSONException {
		
		LightStatusSource lsSource = new LightStatusSource(folderPath);
		LightStatus[] allLightStatuses = lsSource.getAll();
		
		String[] hashTags = HashtagSelector.getHashTags(allLightStatuses);
		Map<String, List<LightStatus>> myMap = new HashMap<String, List<LightStatus>>();		
		
		for(int i=0;i<hashTags.length;i++) {
			myMap.put(hashTags[i], new ArrayList<LightStatus>());
		}
				
		for(int i=0;i<allLightStatuses.length;i++) {
			for(int j=0;j<allLightStatuses[i].hashTags.length;j++){
				LightStatus currentStatus = allLightStatuses[i];				
				String currentHashTag = currentStatus.hashTags[j].toLowerCase();
				
				if(myMap.containsKey(currentHashTag)){						
					myMap.get(currentHashTag).add(currentStatus);
				}					
			}		
		}
						
		for(Map.Entry<String, List<LightStatus>> entry : myMap.entrySet()){
			
			String hashTag = entry.getKey();
			List<LightStatus> lstStatus = entry.getValue();
			
			LightStatus[] statuses = lstStatus.toArray(new LightStatus[lstStatus.size()]);
			List<LightStatus[]> topics = TopicSplitter.splitTopics(hashTag, statuses);
			
			for(int i=0;i<topics.size();i++){
				
				String topicIdentifier = hashTag + "-" + String.valueOf(i);
				TopicAnalyzer analyzer = new TopicAnalyzer(topicIdentifier, topics.get(i));
				
				finalJSON.put(topicIdentifier, analyzer.toJSONObject());					
			}
		}		
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String finalOutput = gson.toJson( finalJSON );
		
		FileOperations.writeFile(finalOutput, "output.txt");
	}

}
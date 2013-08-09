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
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.MapOperations;

public class DataAnalyzer{
	
  	static JSONObject finalJSON = new JSONObject();
		
  	private static Map<String, List<LightStatus>> splitByHashTag(String[] selectedHashTags, LightStatus[] statuses){
  		
  		Map<String, List<LightStatus>> rVal = new HashMap<String, List<LightStatus>>();
  		
  		for(int i=0;i<selectedHashTags.length;i++) {
  			rVal.put(selectedHashTags[i].toLowerCase(), new ArrayList<LightStatus>());
		}
  		
  		for(int i=0;i<statuses.length;i++) {
			for(int j=0;j<statuses[i].hashTags.length;j++){
				LightStatus currentStatus = statuses[i];				
				String currentHashTag = currentStatus.hashTags[j].toLowerCase();
				
				if(rVal.containsKey(currentHashTag)){						
					rVal.get(currentHashTag).add(currentStatus);
				}					
			}		
		}
  		
  		return rVal;
  	}
  	
	public static void analyze(String folderPath) throws IOException, JSONException {
		
		LightStatusSource lsSource = new LightStatusSource(folderPath);
		LightStatus[] allLightStatuses = lsSource.getAll();
		
		String[] hashTags = HashtagSelector.getHashTags(allLightStatuses);
		
		Map<String, List<LightStatus>> myMap = splitByHashTag(hashTags, allLightStatuses);
						
		for(Map.Entry<String, List<LightStatus>> entry : myMap.entrySet()){
			
			String hashTag = entry.getKey();
			List<LightStatus> lstStatus = entry.getValue();
			
			LightStatus[] statuses = lstStatus.toArray(new LightStatus[lstStatus.size()]);
//			List<LightStatus[]> topics = TopicSplitter.splitTopics(hashTag, statuses);
//			
//			for(int i=0;i<topics.size();i++) {
//				String topicIdentifier = hashTag + "-" + String.valueOf(i);
//				TopicAnalyzer analyzer = new TopicAnalyzer(topicIdentifier, topics.get(i));
//				
//				finalJSON.put(topicIdentifier, analyzer.toJSONObject());					
//			}
			
			TopicAnalyzer analyzer = new TopicAnalyzer(hashTag, statuses);
			finalJSON.put(hashTag, analyzer.toJSONObject());
			
		}		
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String finalOutput = gson.toJson( finalJSON );
		
		FileOperations.writeFile(finalOutput, "output.txt");
	}
}
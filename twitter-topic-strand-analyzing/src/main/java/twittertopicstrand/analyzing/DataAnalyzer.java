package twittertopicstrand.analyzing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.HourOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataAnalyzer{
	
  	static String outputDirectory = "/home/twtuser/outputs/";
  	static JSONObject indexJson = new JSONObject();
		
  	private static Map<String, List<LightStatus>> splitByHashTag(String[] selectedHashTags, LightStatus[] statuses){
  		
  		Map<String, List<LightStatus>> rVal = new LinkedHashMap<String, List<LightStatus>>();
  		
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
  	
	public static void analyze(String folderPath) throws Throwable {
		
		File f = new File(outputDirectory);
		if(!f.exists()){
			f.mkdir();
		}
		
		LightStatusSource lsSource = new LightStatusSource(folderPath);
		LightStatus[] allLightStatuses = lsSource.getAll();
		
		String[] hashTags = HashtagSelector.getHashTags(allLightStatuses);
		
		System.out.println("selected hashtags: " + Arrays.toString(hashTags));
		
		Map<String, List<LightStatus>> myMap = splitByHashTag(hashTags, allLightStatuses);
		
		for(Map.Entry<String, List<LightStatus>> entry : myMap.entrySet()) {
			String hashTag = entry.getKey();
			List<LightStatus> lstStatus = entry.getValue();
			
			LightStatus[] statuses = lstStatus.toArray(new LightStatus[lstStatus.size()]);

			List<LightStatus[]> topics = TopicSplitter.splitTopics(statuses);
			
			for(int i=0;i<topics.size();i++) {
				String topicIdentifier = hashTag + "-" + String.valueOf(i);
				
				LightStatus[] topic = topics.get(i);
				TopicAnalyzer analyzer = new TopicAnalyzer(topicIdentifier, topic);
				
				String fileName = outputDirectory + File.separatorChar + topicIdentifier + ".json";
				
				FileOperations.writeFile(analyzer.getMainJson().toString(), fileName);
				indexJson.put( topicIdentifier, analyzer.getIndexJSon() );
			}
		}		
		
		String indexFileName = outputDirectory + File.separatorChar + "@index" + ".json";
		FileOperations.writeFile(indexJson.toString(), indexFileName);
	}
}
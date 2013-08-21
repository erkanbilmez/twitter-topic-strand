package twittertopicstrand.analyzing;

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
	
  	static JSONObject finalJson = new JSONObject();
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
  	
	public static void analyze(String folderPath) throws IOException, JSONException {
		
		LightStatusSource lsSource = new LightStatusSource(folderPath);
		LightStatus[] allLightStatuses = lsSource.getAll();
		
		String[] hashTags = HashtagSelector.getHashTags(allLightStatuses);
		
		System.out.println("selected hashtags:");
		System.out.println(Arrays.toString(hashTags));
		
		Map<String, List<LightStatus>> myMap = splitByHashTag(hashTags, allLightStatuses);
		
		for(Map.Entry<String, List<LightStatus>> entry : myMap.entrySet()){
			
			String hashTag = entry.getKey();
			List<LightStatus> lstStatus = entry.getValue();
			
			LightStatus[] statuses = lstStatus.toArray(new LightStatus[lstStatus.size()]);

			List<LightStatus[]> topics = TopicSplitter.splitTopics(hashTag, statuses);
		
			DateTime start = new DateTime(statuses[0]).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
			
			System.out.println(hashTag);
			System.out.println(topics.size() + "parts");
			for(int i=0;i<topics.size();i++){
				LightStatus[] topic = topics.get(i);
				
				int f1 = HourOperations.getHourId(start, new DateTime(topic[0].createdAt));
				int f2 = HourOperations.getHourId(start, new DateTime(topic[topic.length-1].createdAt));
				
				System.out.println(f1);
				System.out.println(f2);
			}
			
//			for(int i=0;i<topics.size();i++) {
//				String topicIdentifier = hashTag + "-" + String.valueOf(i);
//				
//				TopicAnalyzer analyzer = new TopicAnalyzer(topicIdentifier, statuses);
//				finalJson.put( hashTag, analyzer.getMainJson() );
//				indexJson.put( hashTag, analyzer.getIndexJSon() );
//			}
		}		
		
		String mainOutput = finalJson.toString();
		String indexOutput = indexJson.toString();
		
		FileOperations.writeFile(mainOutput, "/home/twtuser/mainOutput.txt");
		FileOperations.writeFile(indexOutput, "/home/twtuser/indexOutput.txt");
	}
}
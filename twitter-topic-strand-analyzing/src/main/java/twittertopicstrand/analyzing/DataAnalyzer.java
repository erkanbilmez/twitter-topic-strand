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
	
  	static String outputDirectory = "/home/mll2/Desktop/outputs/";
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
  	
  	public static void splitToTopics(String folderPath) throws IOException{
  		LightStatusSource ls = new LightStatusSource(folderPath);
		do{
			String hashTag = FileOperations.getOnlyFileName( ls.getCurrentFileName() );
			
			hashTag = hashTag.substring(0, hashTag.length() - 4 );
			LightStatus[] chunk = ls.getChunk();
		
			List<LightStatus[]> topics = TopicSplitter.splitTopics(chunk);
		}while(ls.iterate());
  	}
		
	public static void analyze(String folderPath) throws Throwable {
		
		File f = new File(outputDirectory);
		if(!f.exists()){
			f.mkdir();
		}

		LightStatusSource ls = new LightStatusSource(folderPath);
		do{
			String topicIdentifier = FileOperations.getOnlyFileName( ls.getCurrentFileName() );
	
			LightStatus[] topic = ls.getChunk();
		
			TopicAnalyzer analyzer = new TopicAnalyzer(topicIdentifier, topic);
			
			String fileName = outputDirectory + File.separatorChar + topicIdentifier + ".json";
			
			FileOperations.writeFile(analyzer.getMainJson().toString(), fileName);
			indexJson.put( topicIdentifier, analyzer.getIndexJSon() );
			
		}while(ls.iterate());
		
		indexJson.put("Parameters", AnalyzingParameters.getParametersAsJson());
		
		String indexFileName = outputDirectory + File.separatorChar + "@index" + ".json";
		FileOperations.writeFile(indexJson.toString(), indexFileName);
	}
}
package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.LightStatus;
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.MapOperations;

public class HashtagSelector {
	
	private static int mostNHashtags = 100;
	
	private static Map<String, Integer> createHashMap(LightStatus[] statuses) throws IOException {
		
		Map<String, Integer> rVal = new HashMap<String, Integer>();		
		
		for(int i=0;i<statuses.length;i++) {
			for(int j=0;j<statuses[i].hashTags.length;j++){
				String currentHashTag = statuses[i].hashTags[j].toLowerCase();
				
				if(!rVal.containsKey(currentHashTag)) {						
					rVal.put(currentHashTag, 1);
				}else{
					rVal.put(currentHashTag, rVal.get(currentHashTag) + 1);
				}		
			}			
		}	
		
		return rVal;
	}
	
	private static Map<String, Integer> createHashMap(String path) throws IOException {
		Map<String, Integer> rVal = new HashMap<String, Integer>();
		LightStatusSource src = new LightStatusSource(path);
		
		do{
			LightStatus[] statuses = src.getChunk();
			
			for(int i=0;i<statuses.length;i++) {
				for(int j=0;j<statuses[i].hashTags.length;j++){
					String currentHashTag = statuses[i].hashTags[j].toLowerCase();
					
					if(!rVal.containsKey(currentHashTag)) {						
						rVal.put(currentHashTag, 1);
					}else{
						rVal.put(currentHashTag, rVal.get(currentHashTag) + 1);
					}		
				}			
			}
			
		}while(src.iterate());
		
			
		
		return rVal;
	}
	
	private static String[] getMostTweetedNHashTags(LightStatus[] statuses, int n) throws IOException {
		String[] rVal = null;
		
		List<String> rValList = new ArrayList<String>();
		
		Map myHashMap = createHashMap(statuses);
		Map<String, Integer> sortedMap = MapOperations.sortMapByValue(myHashMap);
		
		int i = 0;
		
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			String temp = entry.getKey();
			rValList.add(temp);
			i++;
			
			if(i+1 > n) {
				break;
			}
		}
		
		rVal = rValList.toArray(new String[rValList.size()]);
		
		return rVal;		
	}
	
	private static String[] getMostTweetedNHashTags(String path, int n) throws IOException {
		String[] rVal = null;
		
		List<String> rValList = new ArrayList<String>();
		
		Map myHashMap = createHashMap(path);
		Map<String, Integer> sortedMap = MapOperations.sortMapByValue(myHashMap);
		
		int i = 0;
		
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			String temp = entry.getKey();
			rValList.add(temp);
			i++;
			
			if(i+1 > n) {
				break;
			}
		}
		
		rVal = rValList.toArray(new String[rValList.size()]);
		
		return rVal;		
	}
	
	public static String[] getHashTags(LightStatus[] statuses) throws IOException{
		return getMostTweetedNHashTags(statuses, mostNHashtags);
	}
	
	public static String[] getHashTags(String path) throws IOException{
		return getMostTweetedNHashTags(path, mostNHashtags);
	}
	
	public static String[] getFromDisk(String path) throws IOException{
		String[] rVal;
		
		String text = FileOperations.readAllText(path).trim();
				
		text = text.substring(1, text.length()-1);
		String[] subs = text.split(",");
		
		rVal = new String[subs.length];
		for(int i=0;i<subs.length;i++){
			rVal[i] = subs[i].trim();
		}

		return rVal;
	}
}

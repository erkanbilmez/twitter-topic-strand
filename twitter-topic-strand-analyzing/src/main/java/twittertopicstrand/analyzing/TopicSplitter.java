package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.List;

import twitter4j.LightStatus;
import twittertopicstrand.analyzing.TopicAnalyzer.GroupDisc;
import twittertopicstrand.analyzing.TopicAnalyzer.Hour;

public class TopicSplitter { 
	
	int lowThreshold = 3;
	int highThreshold = 50;	
	
	// todo Halil:
	
	public static List<LightStatus[]> splitTopics(String hashTag, LightStatus[] statuses) {
		
		List<LightStatus[]> rVal = new ArrayList<LightStatus[]>();		
		
		rVal.add(statuses);
		
		return rVal;		
	}
}

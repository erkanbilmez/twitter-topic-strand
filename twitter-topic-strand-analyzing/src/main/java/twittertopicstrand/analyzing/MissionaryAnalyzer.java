package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import twitter4j.LightStatus;
import twittertopicstrand.util.HourOperations;

public class MissionaryAnalyzer {	
	
	public int missionaryCount;
	public int[] missionaryCountsByHour;
	public HashSet<Long> missionaries;
	
	public HashSet<Long> getMissionaries(ArrayList<HashMap<Long, Integer>> participants, 
							HashSet<Long> allParticipants, LightStatus[] statuses) throws IOException {
		
		HashSet<Long> rVal = new HashSet<Long>();
		
		HashMap<Long, HashSet<Long>> graph = FollowerGraphCreater.create("/home/mll2/Desktop/reduced_rel.csv");
		
		HashSet<Long> processedUsers = new HashSet<Long>();
		HashMap<Long, Double> scores = new HashMap<Long, Double>();		
		HashMap<Long, Integer> window = new HashMap<Long, Integer>();
		HashSet<Long> following;		
		
		ArrayList<Long> tempList = new ArrayList<Long>();
				
		int firstDateIndex = 0;
		long userId = 0;
		int hourDiff = 0;
		
		for(int i=0;i<statuses.length;i++) {
			
			hourDiff = HourOperations.getHourId( statuses[firstDateIndex].createdAt, statuses[i].createdAt );
			while(hourDiff > AnalyzingParameters.missionaryWindowSize){				
				firstDateIndex ++;
				int count = window.get(statuses[firstDateIndex].userId);
				if(count == 1){
					window.remove(statuses[firstDateIndex].userId);
				}else{
					window.put(statuses[firstDateIndex].userId, count - 1);
				}	
				hourDiff = HourOperations.getHourId( statuses[firstDateIndex].createdAt, statuses[i].createdAt );
			}
			
			userId = statuses[i].userId;
						
			if(!processedUsers.contains(userId)){
				processedUsers.add(userId);	
				
				following = graph.get(userId);
				
				if(following != null && following.size() > 0){
					if(following.size() < window.size()){
						for(Long foll: following){
							if(window.keySet().contains(foll)){
								tempList.add(foll);
							}
						}
					}else{
						for(Long foll: window.keySet()){
							if(following.contains(foll)){
								tempList.add(foll);
							}
						}
					}
					
					double score = 1.0 / tempList.size();
					
					for(Long l: tempList){
						double current = scores.containsKey(l) ? scores.get(l) : 0.0;
						scores.put(l, current + score);
					}
					
					tempList.clear();
				}
			}
			
			// put the new user ..
			int count = window.containsKey(userId) ? window.get(userId) : 0;
			window.put(statuses[i].userId, count + 1);
		}	
		
		double threshold = allParticipants.size() * AnalyzingParameters.missionaryThresholdFraction;
		
		for(Entry<Long, Double> item: scores.entrySet()) {
			if(item.getValue() > threshold) {
				rVal.add(item.getKey());
			}
		}
		
		return rVal;
	}
	
	public void analyze(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants, LightStatus[] statuses) throws IOException {
		
		this.missionaryCountsByHour = new int[participants.size()];
		
		this.missionaries = getMissionaries(participants, allParticipants, statuses);
		this.missionaryCount = this.missionaries.size();
		
		int i = 0;
		for(HashMap<Long, Integer> hour: participants){
			int temp = 0;
			for(Long userId: hour.keySet()){
				if(this.missionaries.contains ( userId )){
					temp ++;
				}
			}
			this.missionaryCountsByHour[i] = temp;
			i++;
		}
	}
}

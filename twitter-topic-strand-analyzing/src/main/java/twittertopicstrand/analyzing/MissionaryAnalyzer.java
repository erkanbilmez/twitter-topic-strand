package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import twitter4j.LightStatus;
import twittertopicstrand.util.HourOperations;

public class MissionaryAnalyzer {

	public int windowSize = 12; //hours
	public double thresholdFraction = 0.05;
	
	public int missionaryCount;
	public int[] missionaryCountsByHour;
	public HashSet<Long> missionaries;
	
	public void processThisUser(LightStatus[] statuses, HashSet<Long> follows, 
														HashMap<Long, Double> scores, int index){		
		ArrayList<Long> scorers = new ArrayList<Long>();
		
		for(int i=index-1;i>-1;i--){	
			if ( HourOperations.getHourId(statuses[i].createdAt, statuses[index].createdAt) > windowSize) {
				break;
			}
						
			if (follows != null && follows.contains( statuses[i].userId )) {
				scorers.add(statuses[i].userId);
			}
		}	
		
		if(scorers.size() > 0) {		
			double score = 1.0 / scorers.size();			
			for(Long scorerId: scorers){
				double old = scores.containsKey(scorerId) ? scores.get(scorerId) : 0.0;
				scores.put(scorerId, old + score);
			}
		}
	}
	
	public HashSet<Long> getMissionaries(ArrayList<HashMap<Long, Integer>> participants, 
							HashSet<Long> allParticipants, LightStatus[] statuses) throws IOException {
		
		HashSet<Long> rVal = new HashSet<Long>();
		
		HashMap<Long, HashSet<Long>> graph = FollowerGraphCreater.create("/home/mll2/Desktop/reduced_rel.csv");
		
		HashSet<Long> processedUsers = new HashSet<Long>();
		HashMap<Long, Double> scores = new HashMap<Long, Double>();
		
		int counter=0;
		for(int i=0;i<statuses.length;i++) {
			
			long userId = statuses[i].userId;
			
			if(!processedUsers.contains(userId)){
				processedUsers.add(userId);
				
				processThisUser(statuses, graph.get(userId), scores, i);
			}
			
			counter++;
			if(counter % 1000 == 0) {
				System.out.println(counter);
			}
		}	
		
		for(Entry<Long, Double> item: scores.entrySet()) {
			if(item.getValue() > 30) {
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

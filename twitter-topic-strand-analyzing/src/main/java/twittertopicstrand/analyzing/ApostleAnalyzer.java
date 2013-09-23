package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twitter4j.LightStatus;
import twittertopicstrand.util.HourOperations;

public class ApostleAnalyzer {
	
	public int apostleCount;
	public int[] apostleCountsByHour;
	public HashSet<Long> apostles;
	
	public HashMap<Long, List<LightStatus>> divideRetweetGroups(LightStatus[] statuses){
		HashMap<Long, List<LightStatus>> rVal = new HashMap<Long, List<LightStatus>>();
		
		for(int i=0;i<statuses.length;i++){
			if(! rVal.containsKey( statuses[i].retweetedStatusId) ){
				rVal.put(statuses[i].retweetedStatusId, new ArrayList<LightStatus>());
			}
			
			rVal.get(statuses[i].retweetedStatusId).add(statuses[i]);
		}
		
		return rVal;
	}
	
	public LightStatus[] getOnlyRetweets(LightStatus[] statuses) {
		
		ArrayList<LightStatus> rVal = new ArrayList<LightStatus>();
		
		for(int i=0;i<statuses.length;i++) {
			if(statuses[i].retweetedStatusId != -1) {
				rVal.add(statuses[i]);
			}
		}		
		
		return rVal.toArray(new LightStatus[rVal.size()]);		
	}	
	
	public HashSet<Long> getApostles(ArrayList<HashMap<Long, Integer>> participants, 
							HashSet<Long> allParticipants, LightStatus[] statuses) throws IOException {
		
		HashSet<Long> rVal = new HashSet<Long>();
		
		HashMap<Long, HashSet<Long>> graph = FollowerGraphCreater.create("/home/mll2/Desktop/reduced_rel.csv");
		
		LightStatus[] onlyRetweets = getOnlyRetweets(statuses);
		
		System.out.println(onlyRetweets.length);
		
		HashMap<Long, List<LightStatus>> retweetsDivided = divideRetweetGroups(onlyRetweets);
		
		for(Entry<Long, List<LightStatus>> item: retweetsDivided.entrySet()){
			
		}
		
		return rVal;
	}
	
	public void analyze(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants, LightStatus[] statuses) throws IOException {
		
		this.apostleCountsByHour = new int[participants.size()];
		
		this.apostles = getApostles(participants, allParticipants, statuses);
		this.apostleCount = this.apostles.size();
		
		int i = 0;
		for(HashMap<Long, Integer> hour: participants){
			int temp = 0;
			for(Long userId: hour.keySet()){
				if(this.apostles.contains ( userId )){
					temp ++;
				}
			}
			this.apostleCountsByHour[i] = temp;
			i++;
		}
	}
}

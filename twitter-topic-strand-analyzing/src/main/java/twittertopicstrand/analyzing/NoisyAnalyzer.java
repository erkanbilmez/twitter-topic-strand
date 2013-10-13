package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import twitter4j.LightStatus;

public class NoisyAnalyzer {

	public int noisyCount;
	public int[] noisyCountsByHour;
	public HashSet<Long> noisies;
	
	private HashSet<Long> getNoisies(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants, LightStatus[] statuses) {
		HashSet<Long> rVal = new HashSet<Long>();

		double mean = (double)statuses.length / (double)allParticipants.size();
		
		HashMap<Long, Integer> tweetCounts = new HashMap<Long, Integer>();
		
		for(int i=0;i<statuses.length;i++){
			Long userId = statuses[i].userId;
			int count = tweetCounts.containsKey(userId) ? tweetCounts.get(userId) : 0;
			tweetCounts.put(userId, count + 1);
		}
		
		double stdDeviation = 0;
		
		for(Map.Entry<Long,Integer> entry:tweetCounts.entrySet()){
			double temp = entry.getValue();
			stdDeviation += Math.pow(mean - temp, 2);
		} 
		
		stdDeviation = Math.sqrt((double)stdDeviation / (double)tweetCounts.size());
		
		double minTweetForNoisy = mean + AnalyzingParameters.noisyK * stdDeviation;
		
		for(Map.Entry<Long,Integer> entry:tweetCounts.entrySet()){
			if(entry.getValue() >= minTweetForNoisy){
				rVal.add(entry.getKey());
			}
		}
		
		return rVal;
	}
	
	public void analyze(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants, LightStatus[] statuses) {
		
		this.noisyCountsByHour = new int[participants.size()];
		
		this.noisies = getNoisies(participants, allParticipants, statuses);
		this.noisyCount = this.noisies.size();
		
		int i = 0;
		for(HashMap<Long, Integer> hour: participants){
			int temp = 0;
			for(Long userId: hour.keySet()){
				if(this.noisies.contains ( userId )){
					temp ++;
				}
			}
			this.noisyCountsByHour[i] = temp;
			i++;
		}
	}
}

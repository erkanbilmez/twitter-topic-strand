package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import twitter4j.LightStatus;

public class HeroAnalyzer {
	
	int hero_ratio = 10;
	
	public int heroCount;
	public int[] heroCountsByHour;
	public HashSet<Long> heroes;
	
	private HashSet<Long> getHeroes(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants, LightStatus[] statuses) {
		HashSet<Long> rVal = new HashSet<Long>();

		int averageTweetCount = statuses.length / allParticipants.size();
		int minTweetForHero = averageTweetCount * hero_ratio;
		
		HashMap<Long, Integer> tweetCounts = new HashMap<Long, Integer>();
		
		for(int i=0;i<statuses.length;i++){
			Long userId = statuses[i].userId;
			int count = tweetCounts.containsKey(userId) ? tweetCounts.get(userId) : 0;
			tweetCounts.put(userId, count + 1);
		}
		
		for(Map.Entry<Long,Integer> entry:tweetCounts.entrySet()){
			if(entry.getValue() >= minTweetForHero){
				rVal.add(entry.getKey());
			}
		}
		
		return rVal;
	}
	
	public void analyze(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants, LightStatus[] statuses) {
		
		this.heroCountsByHour = new int[participants.size()];
		
		this.heroes = getHeroes(participants, allParticipants, statuses);
		this.heroCount = this.heroes.size();
		
		int i = 0;
		for(HashMap<Long, Integer> hour: participants){
			int temp = 0;
			for(Long userId: hour.keySet()){
				if(this.heroes.contains ( userId )){
					temp ++;
				}
			}
			this.heroCountsByHour[i] = temp;
			i++;
		}
	}
}
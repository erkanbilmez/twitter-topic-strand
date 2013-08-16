package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class VeteranAnalyzer {
	float vetr_fraction = 0.9f;
	int vetr_segcount = 10;
	
	public int veteranCount;
	public int[] veteranCountsByHour;
	public HashSet<Long> veterans;
	
	private HashSet<Long> getVeterans(ArrayList<HashMap<Long, Integer>> participants) {
		
		int numOfChunks = vetr_segcount;
		int numOfItemsPerChunk = participants.size() / numOfChunks;
		int minChunkCountForVeteran = (int) (numOfChunks * vetr_fraction);
			
		HashSet<Long> rVal = new HashSet<Long>();
		
		ArrayList<HashSet<Long>> temp = new ArrayList<HashSet<Long>>(numOfChunks);
		HashMap<Long, Integer> fTemp = new HashMap<Long, Integer>();
		
		for(int i=0;i<numOfChunks;i++){
			temp.add(new HashSet<Long>());
		}
			
		for(int i=0;i<participants.size();i++){
			
			int chunkId = i / numOfItemsPerChunk;
		
			if(chunkId < numOfChunks){
				for(Map.Entry<Long, Integer> entry: participants.get(i).entrySet() ){
					long userId = entry.getKey();
			
					if(!temp.get(chunkId).contains(userId)){
						temp.get(chunkId).add(userId);
					}
				}			
			}
		}
		
		for(int i=0;i<temp.size();i++){
			for(Long userId: temp.get(i)){
				int count = fTemp.containsKey(userId) ? fTemp.get(userId) : 0;
				fTemp.put(userId, count + 1);
			}			
		}
		
		for(Map.Entry<Long, Integer> entry: fTemp.entrySet()){
			if(entry.getValue() > minChunkCountForVeteran){
				rVal.add(entry.getKey());
			}
		}
		
		return rVal;
	}
	
	public void analyze(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants) {
		
		this.veteranCountsByHour = new int[participants.size()];
		
		this.veterans = getVeterans(participants);
		this.veteranCount = this.veterans.size();
		
		int i = 0;
		for(HashMap<Long, Integer> hour: participants){
			int temp = 0;
			for(Long userId: hour.keySet()){
				if(veterans.contains ( userId )){
					temp ++;
				}
			}
			this.veteranCountsByHour[i] = temp;
			i++;
		}
	}
}

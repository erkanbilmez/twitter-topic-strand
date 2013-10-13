package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class VeteranAnalyzer {

	public int veteranCount;
	public int[] veteranCountsByHour;
	public HashSet<Long> veterans;
	
	private HashSet<Long> getVeterans(ArrayList<HashMap<Long, Integer>> hourlyParticipants) throws Exception {
		int numOfChunks = AnalyzingParameters.veteranSegCount;
		int numOfItemsPerChunk = hourlyParticipants.size() / numOfChunks;
		int minChunkCountForVeteran = (int) (numOfChunks * AnalyzingParameters.veteranFraction);
			
		HashSet<Long> rVal = new HashSet<Long>();
		
		if(numOfItemsPerChunk == 0){
			throw new Exception("numOfItemsPerChunk cannot be zero.");
		}
		
		int[] numOfItemsPerChunkArr = new int[numOfChunks];
		
		for(int i=0;i<numOfItemsPerChunkArr.length;i++){
			numOfItemsPerChunkArr[i] = numOfItemsPerChunk;
		}
		
		int remainder = hourlyParticipants.size() - (numOfItemsPerChunk * numOfChunks);
		
		for(int i=0;i<remainder;i++){
			numOfItemsPerChunkArr[i]++;
		}
		
		ArrayList<HashSet<Long>> temp = new ArrayList<HashSet<Long>>(numOfChunks);
		HashMap<Long, Integer> fTemp = new HashMap<Long, Integer>();
		
		for(int i=0;i<numOfChunks;i++){
			temp.add(new HashSet<Long>());
		}
		
		int index = 0;
		for(int i=0;i<numOfItemsPerChunkArr.length;i++){
			int chunkId = i;
			
			for(int j=0;j<numOfItemsPerChunkArr[i];j++){
				for(Map.Entry<Long, Integer> entry: hourlyParticipants.get(index).entrySet()){
					long userId = entry.getKey();
					
					if(!temp.get(chunkId).contains(userId)){
						temp.get(chunkId).add(userId);
					}
				}
				index++;
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
	
	public void analyze(ArrayList<HashMap<Long, Integer>> participants, HashSet<Long> allParticipants) throws Exception {
		
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

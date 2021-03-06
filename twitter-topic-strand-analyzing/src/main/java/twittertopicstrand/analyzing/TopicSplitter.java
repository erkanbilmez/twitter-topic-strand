package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.HourOperations;
import twittertopicstrand.util.Pair;

public class TopicSplitter { 
	
	static int windowSize = 10;
	static int minTopicLength = 10; // hours
	
	static double lowThreshold = 10;
	static double highThreshold = 50;	
	
	static int[] firstIndexOfHours;
	
	public static int[] createArray(LightStatus[] statuses){
		int[] rVal;
		
		DateTime start = new DateTime(statuses[0].createdAt);
		DateTime end = new DateTime(statuses[statuses.length-1].createdAt);
	
		int length = HourOperations.getHourId(start, end) + 1;
		
		rVal = new int[length];
		firstIndexOfHours = new int[length];
		
		for(int i=0;i<firstIndexOfHours.length;i++){
			firstIndexOfHours[i] = -1;
		}
		
		for(int i=0;i<statuses.length;i++){
			int hourId = HourOperations.getHourId(start, new DateTime ( statuses[i].createdAt ));
			if(rVal[hourId] == 0) {
				firstIndexOfHours[hourId] = i;
			}
			rVal[hourId]++;
		}
	
		return rVal;
	}
	
	public static double[] SumPastNFilter(int[] arr){
		double[] rVal = new double[arr.length];
		
		for(int i=0;i<windowSize-1;i++){
			rVal[i] = arr[i];
		}
		
		for(int i=windowSize-1;i<arr.length;i++){
			for(int j=i-windowSize+1;j<=i;j++){
				rVal[i] += arr[j];
			}
			rVal[i] /= windowSize;
		}
		
		return rVal;
	}
	
	public static void addSubset(int start, int end, List<Pair<Integer,Integer>> pairs){
		int from = firstIndexOfHours[start];
		int to = firstIndexOfHours[end];
		
		while(from==-1){
			start++;
			from = firstIndexOfHours[start];
		}
		while(to ==-1){
			end--;
			to = firstIndexOfHours[end];
		}
		
		if( end - start > minTopicLength ) {
			Pair<Integer, Integer> pair = new Pair<Integer, Integer>(start,end);
			pairs.add(pair);
		}
	}
	
	public static List<Pair<Integer, Integer>> getCoordinates(double[] filtered){
		List<Pair<Integer, Integer>> rVal = new ArrayList<Pair<Integer, Integer>>();
		
		int state=0; //0 is not in list, 1 is in list waiting for high, 2 is in list definitely
		int start=0;
		
		int i=0;
		for( i=0;i<filtered.length;i++ ) {
			double current = filtered[i];
			if(state==0){
				if(current>highThreshold){
					state = 2;
					start = i;
				}else if(current>lowThreshold){
					state = 1;
					start = i;
				}else{
					start = 0; state = 0;
				}
			}else if(state == 1){
				if(current>highThreshold){
					state = 2;
				}else if(current > lowThreshold){
					// do nothing, continue..
				}else{
					start = 0; state = 0;
				}
			}else if(state == 2){
				if(current>highThreshold){
					// do nothing, continue..
				}else if(current>lowThreshold){
					// do nothing, continue..
				}else{
					addSubset(start, i-1, rVal);
					start = 0; state = 0;
				}
			}
		}
		
		if( state == 2 ) {
			addSubset(start, i-1, rVal);
		}
		
		return rVal;	
	}
	
	public static List<LightStatus[]> getParts(LightStatus[] statuses, List<Pair<Integer,Integer>> pairs){
		List<LightStatus[]> rVal = new ArrayList<LightStatus[]> ();
		
		for(Pair<Integer,Integer> pair : pairs){
			int left = pair.getLeft();
			int right = pair.getRight();
		
			int from = firstIndexOfHours[left];
			int to = firstIndexOfHours[right];
			
			LightStatus[] temp = Arrays.copyOfRange(statuses, from, to+1);
			rVal.add(temp);
		}
		
		return rVal;
	}
	
	static int[] arr;
	
	public static List<LightStatus[]> splitTopics(LightStatus[] statuses) {
		
		List<LightStatus[]> rVal;
		
		arr = createArray(statuses);
		
		if( arr.length < minTopicLength )
			return new ArrayList<LightStatus[]>();
		
		double[] filtered = SumPastNFilter(arr);
		
		List<Pair<Integer,Integer>> pairs = getCoordinates(filtered);
		rVal = getParts(statuses, pairs);

		return rVal;
	}
}
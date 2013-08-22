package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twittertopicstrand.util.HourOperations;

public class TopicSplitter { 
	
	static double lowThreshold = 3;
	static double highThreshold = 50;	
	
	static int[] firstIndexOfHours;
	
	private static int[] createArray(LightStatus[] statuses){
		int[] rVal;
		
		DateTime start = new DateTime(statuses[0].createdAt).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		DateTime end = new DateTime(statuses[statuses.length-1].createdAt).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		
		int length = HourOperations.getHourId(start, end) + 1;
		
		rVal = new int[length];
		firstIndexOfHours = new int[length];
		
		for(int i=0;i<statuses.length;i++){
			int hourId = HourOperations.getHourId(start, new DateTime ( statuses[i].createdAt ));
			if(rVal[hourId] == 0){
				firstIndexOfHours[hourId] = i;
			}
			rVal[hourId]++;
		}
		
		return rVal;
	}
	
	private static double[] SumPastNFilter(int[] arr, int windowSize){
		double[] rVal = new double[arr.length];
		
		for(int i=0;i<windowSize;i++){
			rVal[i] = arr[0];
		}
		
		for(int i=windowSize;i<arr.length;i++){
			for(int j=i-windowSize+1;j<=i;j++){
				rVal[i] += arr[j];
			}
			rVal[i] /= windowSize;
		}
		
		return rVal;
	}

	public static LightStatus[] getSubset(LightStatus[] statuses, int start, int end){
		LightStatus[] rVal;
		
		int from = firstIndexOfHours[start];
		int to = firstIndexOfHours[end];
		
		rVal = Arrays.copyOfRange(statuses, from, to);
		
		return rVal;
	}
	
	public static List<LightStatus[]> splitTopics(String hashTag, LightStatus[] statuses) {
		List<LightStatus[]> rVal = new ArrayList<LightStatus[]>();
		
		int[] arr = createArray(statuses);
		
		System.out.println("original: " + Arrays.toString(arr));
		int k = 10;
		int minTopicLength = 10;
		
		double[] filtered = SumPastNFilter(arr, k);
		System.out.println("filtered: " + Arrays.toString(filtered));
		
		int state=0; //0 is not in list, 1 is in list waiting for high, 2 is in list definitely
		int start=0;
		int end=0;
		
		for(int i=0;i<filtered.length;i++) {
			double current = filtered[i];
			if(state==0){
				if(current>highThreshold){
					state = 2;
					start = i;
				}else if(current>lowThreshold){
					state = 1;
					start = i;
				}else{
					start = 0; end = 0; state = 0;
				}
			}else if(state == 1){
				if(current>highThreshold){
					state = 2;
				}else if(current > lowThreshold){
					// do nothing, continue..
				}else{
					start = 0; end = 0; state = 0;
				}
			}else if(state == 2){
				if(current>highThreshold){
					// do nothing, continue..
				}else if(current>lowThreshold){
					// do nothing, continue..
				}else{
					end = i-1;
					if(end-start>minTopicLength){
						System.out.println(start + "," + end);
						LightStatus[] subset = getSubset(statuses, start, end);
						rVal.add(subset);
					}
					start = 0; end = 0; state = 0;
				}
			}
		}
		
		if(state ==2) {
			end = filtered.length - 1;
			if(end-start>minTopicLength){
				System.out.println(start + "," + end);
				LightStatus[] subset = getSubset(statuses, start, end);
				rVal.add(subset);
			}
		}
		
		return rVal;
	}
}
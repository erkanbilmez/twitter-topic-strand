package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import twitter4j.LightStatus;
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.HourOperations;

public class ActivityAnalyzer {
	public static void analyzeAll(String[] arr) throws IOException{
		String lightStatusSourceDir = "/home/twtuser/lightstatus-subset";
		LightStatusSource src = new LightStatusSource(lightStatusSourceDir);
		
		LightStatus[] statuses = src.getAll();
		
		for(int i=0;i<arr.length;i++){
			analyze(statuses, arr[i]);
		}
	}
	
	public static void analyze(LightStatus[] statuses, String hashTag) throws IOException{
		
		List<LightStatus> temp = new ArrayList<LightStatus>();
		for(int i=0;i<statuses.length;i++){
			if( Arrays.asList( statuses[i].hashTags ).contains(hashTag)){
				temp.add(statuses[i]);
			}
		}
		
		LightStatus[] subset = temp.toArray(new LightStatus[temp.size()]);
		DateTime start = new DateTime(subset[0].createdAt);
		DateTime end = new DateTime(subset[subset.length-1].createdAt);
	
		int length = HourOperations.getHourId(start, end) + 1;
		
		System.out.println("hashtag:" + hashTag);
		System.out.println("start:" + start);
		System.out.println("end:" + end);
		System.out.println("length" + length);		
		System.out.println("------");
		
		int[] activity = new int[length];
		
		for(int i=0;i<subset.length;i++){
			int hourId = HourOperations.getHourId(start, new DateTime(subset[i].createdAt));
			activity[hourId]++;
		}
		
		String line = "createPdf('" + hashTag + "'," + gettRArr(activity) + "," + start.getHourOfDay() + ")";
		FileOperations.addLine(line, "/home/twtuser/createPdf.r");
	}
	
	private static String gettRArr(int[] arr){
		String rVal = "c(";

		for(int i=0;i<arr.length-1;i++){
			rVal += String.valueOf(arr[i]) + ",";
		}
		rVal += String.valueOf(arr[arr.length-1]) + ")";

		return rVal;
	}

}

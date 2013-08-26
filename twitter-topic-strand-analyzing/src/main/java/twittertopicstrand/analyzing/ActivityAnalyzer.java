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
import twittertopicstrand.util.Pair;

public class ActivityAnalyzer {
	static void analyze(String folderPath) throws IOException {
		LightStatusSource lsSource = new LightStatusSource(folderPath); 
		
		do{
			LightStatus[] subset = lsSource.getChunk();
			String hashTag = FileOperations.getOnlyFileName(lsSource.getCurrentFileName());
			
			DateTime start = new DateTime(subset[0].createdAt);
			
			int[] arr = TopicSplitter.createArray(subset);
			
			if( arr.length < TopicSplitter.minTopicLength )
				return;
			
			double[] filtered = TopicSplitter.SumPastNFilter(arr);
			
			List<Pair<Integer,Integer>> pairs = TopicSplitter.getCoordinates(filtered);
				
			String line = "createPng('" + hashTag + "'," + 
						gettRArrFromArray(arr) + "," + 
							start.getHourOfDay() + "," + 
								getRArrFromPairs(pairs) + ")";
			
			FileOperations.addLine(line, "/home/twtuser/createPng.r");
			
			
		}while(lsSource.iterate());
	}
	
	private static String gettRArrFromArray(int[] arr){
		String rVal = "c(";

		for(int i=0;i<arr.length-1;i++){
			rVal += String.valueOf(arr[i]) + ",";
		}
		rVal += String.valueOf(arr[arr.length-1]) + ")";

		return rVal;
	}
	
	private static String getRArrFromPairs(List<Pair<Integer,Integer>> pairs){
		String rVal = "c(";
		
		int i=0;
		for(i=0;i<pairs.size()-1;i++){
			Pair<Integer,Integer> pair = pairs.get(i);
			rVal += pair.getLeft() + "," + pair.getRight() + ",";
		}
		
		Pair<Integer,Integer> pair = pairs.get(i);
		rVal += pair.getLeft() + "," + pair.getRight() + ")";
		
		return rVal;
	}

}

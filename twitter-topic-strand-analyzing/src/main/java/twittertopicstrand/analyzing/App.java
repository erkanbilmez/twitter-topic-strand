package twittertopicstrand.analyzing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.converter.BilkentReducedToLightStatusConverter;
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.HourOperations;

/**
 * Hello world!
 *
 */
public class App {

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
		DateTime start = new DateTime(subset[0].createdAt).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		DateTime end = new DateTime(subset[subset.length-1].createdAt).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
	
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
		
	public static void convert() throws IOException{
		
    	String src = "/home/twtuser/reduced-tweets-subset";
    	String dest = "/home/twtuser/lightstatus-subset";
    	
    	BilkentReducedToLightStatusConverter.convert(src,dest,true);
	}
	
    public static void main( String[] args ) throws IOException, JSONException {    	
    	
    	System.out.println("hello ..");
   
    	String[] arr = new String[] { "direngeziparkı", "direngeziparki", 
    			"occupygezi", "direnankara", "redhack", "direngezi", 
    			"direngaziparki", "direngeziseninleyiz", "direnizmir", 
    			"sesvertürkiyebuülkesahipsizdeğil", "direnbesiktas", 
    			"bubirsivildirenis", "occupyturkey", "tayyipistifa", 
    			"cevapver", "yenibirdünya", "eylemvakti","atatürki̇yi̇ki̇varsin", 
    			"tayyi̇pi̇yi̇ki̇varsin", "direnturkiye", "1milyonyarintaksime", 
    			"weareerdoğan", "turkey", "tayipi̇stifa", "occupylondon", 
    			"geziparki", "direnadana", "ff", "türkiyemdireniyor", "rt" };
    	
    	analyzeAll(arr);
    	
    	//String statusSourceDir = "/home/sait//Desktop/data";
    	
    	String lightStatusSourceDir = "/home/twtuser/lightstatus-subset";
    	
    	//StatusSource.convertLightStatusSource(statusSourceDir, lightStatusSourceDir, true);
    	//DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
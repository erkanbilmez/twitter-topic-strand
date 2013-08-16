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
import twittertopicstrand.util.HourOperations;

/**
 * Hello world!
 *
 */
public class App {

	public static void analyze() throws IOException{
		String lightStatusSourceDir = "/home/twtuser/lightstatus-subset-all";
		LightStatusSource src = new LightStatusSource(lightStatusSourceDir);
		
		LightStatus[] statuses = src.getAll();
		
		List<LightStatus> temp = new ArrayList<LightStatus>();
		for(int i=0;i<statuses.length;i++){
			if( Arrays.asList( statuses[i].hashTags ).contains("direngeziparkı")){
				temp.add(statuses[i]);
			}
		}
		LightStatus[] subset = temp.toArray(new LightStatus[temp.size()]);
		int length = HourOperations.getHourId(new DateTime(subset[0].createdAt), 
												new DateTime(subset[subset.length-1].createdAt)) + 1;
				
		DateTime start = new DateTime(subset[0].createdAt);
		int[] activity = new int[length];
		
		for(int i=0;i<subset.length;i++){
			int hourId = HourOperations.getHourId(start, new DateTime(subset[i].createdAt));
			activity[hourId]++;
		}
		
		System.out.println(Arrays.toString(activity));
	}
		
	public static void convert() throws IOException{
		
    	String src = "/home/sait/Desktop/reducedSubset";
    	String dest = "/home/sait/Desktop/lightstatus-subset";
    	
    	BilkentReducedToLightStatusConverter.convert(src,dest,true);
	}
	
    public static void main( String[] args ) throws IOException, JSONException {    	
    	
    	System.out.println("hello ..");
    	
    	analyze();
 
    	//String statusSourceDir = "/home/sait//Desktop/data";
    	
    	//String lightStatusSourceDir = "/home/twtuser/lightstatus-subset";
    	
    	//String lightStatusSourceDir = "/home/sait/Desktop/lightstatus-subset";
    	
    	//StatusSource.convertLightStatusSource(statusSourceDir, lightStatusSourceDir, true);
    	//DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
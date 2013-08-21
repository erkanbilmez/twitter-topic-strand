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
import twittertopicstrand.sources.StatusSource;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.HourOperations;

/**
 * Hello world!
 *
 */
public class App {
		
	public static void convert() throws IOException{
		
    	String src = "/home/twtuser/reduced-tweets-subset";
    	String dest = "/home/twtuser/lightstatus-subset";
    	
    	BilkentReducedToLightStatusConverter.convert(src,dest,true);
	}
	
    public static void main( String[] args ) throws IOException, JSONException {    	
    	System.out.println("hello ..");
   
    	//String statusSourceDir = "/home/sait//Desktop/data";
    	
    	String lightStatusSourceDir = "/home/twtuser/lightstatus-subset";
    	
    	//StatusSource.convertLightStatusSource(statusSourceDir, lightStatusSourceDir, true);
    	DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
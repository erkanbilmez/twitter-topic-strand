package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.converter.BilkentReducedToLightStatusConverter;
import twittertopicstrand.converter.BilkentToLightStatusConverter;
import twittertopicstrand.converter.BilkentToStatusConverter;
import twittertopicstrand.sources.StatusSource;
import twittertopicstrand.util.FileOperations;

/**
 * Hello world!
 *
 */
public class App {
		
    public static void main( String[] args ) throws IOException, JSONException {    	
    	
    	System.out.println("hello");
    	
    	String src = "/home/twtuser/reduced-tweets-subset";
    	String dest = "/home/twtuser/lightstatus-subset";
    	
    	BilkentReducedToLightStatusConverter.convert(src,dest,true);
    	
//    	System.out.println("hello world");
//
//    	String statusSourceDir = "/home/sait//Desktop/data";
//    	String lightStatusSourceDir = "/home/sait/Desktop/data2";
//    	
//    	//StatusSource.convertLightStatusSource(statusSourceDir, lightStatusSourceDir, true);
//    	DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
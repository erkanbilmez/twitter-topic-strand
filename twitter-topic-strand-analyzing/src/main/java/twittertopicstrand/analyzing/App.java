package twittertopicstrand.analyzing;

import java.io.IOException;

import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import twitter4j.LightStatus;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException, JSONException {
    	
    	String statusSourceDir = "/home/mll2/Desktop/x";
    	String lightStatusSourceDir = "/home/mll2/Desktop/x2";
    	
    	//StatusSource.convertLightStatusSource(statusSourceDir, lightStatusSourceDir, true);
    	DataAnalyzer.analyze(lightStatusSourceDir);    	
    }
}
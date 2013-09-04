package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.joda.time.DateTime;

import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twittertopicstrand.converter.BilkentReducedToLightStatusConverter;
import twittertopicstrand.converter.HashTagRemover;
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.HourOperations;

public class App {
	
	public static void convert() throws IOException{
    	String src = "/home/twtuser/reduced-tweets";
    	String dest = "/home/twtuser/lightstatuses";
    	String finalDest = "/home/twtuser/lightstatuses-removed-splitted";
    	
    	BilkentReducedToLightStatusConverter.convert(src, dest, true);
    	
    	String[] hashTags = HashtagSelector.getFromDisk("/home/twtuser/hashTags.txt");
    	
    	HashTagSplitter.convert(dest, finalDest, hashTags);
	}
	
    public static void main( String[] args ) throws Throwable {    	
    	System.out.println("hello ..");
    	
    	String lightStatusSourceDir = "/home/twtuser/lightstatuses-removed-splitted-subset";
    	DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
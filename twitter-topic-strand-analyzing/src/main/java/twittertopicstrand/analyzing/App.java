package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import twitter4j.internal.org.json.JSONException;
import twittertopicstrand.converter.BilkentReducedToLightStatusConverter;
import twittertopicstrand.converter.HashTagRemover;
import twittertopicstrand.sources.LightStatusSource;

public class App {
		
	public static void split() throws IOException{
		String src = "/home/twtuser/lightstatuses-removed";
    	String dest = "/home/twtuser/lightstatuses-removed-splitted";
    	
    	String[] hashTags = HashtagSelector.getFromDisk("/home/twtuser/hashTags.txt");
    	
    	System.out.println(Arrays.toString(hashTags));
    	
    	HashTagSplitter.convert(src, dest, hashTags);
	}
	
	public static void convert() throws IOException{
    	String src = "/home/twtuser/reduced-tweets";
    	String dest = "/home/twtuser/lightstatuses";
    	String finalDest = "/home/twtuser/lightstatuses-removed";
    	
    	BilkentReducedToLightStatusConverter.convert(src, dest, true);
    	
    	String[] hashTags = HashtagSelector.getHashTags(dest);
    	
    	System.out.println(Arrays.toString(hashTags));
    
    	HashTagRemover.convert(dest, finalDest, hashTags);
	}
	
	public static void remove() throws IOException {
		String src = "/home/twtuser/lightstatus-subset";
		String dest = "/home/twtuser/lightstatus-subset-removed";
		
		String[] arr = new String[] 
    			{ "direngeziparkı", "direngeziparki", "occupygezi", 
    			"direnankara", "redhack", "direngezi", "direngaziparki", 
    			"direngeziseninleyiz", "direnizmir", "sesvertürkiyebuülkesahipsizdeğil", 
    			"direnbesiktas", "bubirsivildirenis", "occupyturkey", 
    			"tayyipistifa", "cevapver" };
    
    	HashTagRemover.convert(src, dest, arr);
	}
	
	public static void analyzeActivity() throws IOException{
		String path = "/home/twtuser/lightstatus-subset-removed";
		String[] arr = new String[] { "direngeziparkı", "direngeziparki", "occupygezi", 
    			"direnankara", "redhack", "direngezi", "direngaziparki", 
    			"direngeziseninleyiz", "direnizmir", "sesvertürkiyebuülkesahipsizdeğil", 
    			"direnbesiktas", "bubirsivildirenis", "occupyturkey", 
    			"tayyipistifa", "cevapver" };
		ActivityAnalyzer.analyzeAll(path, arr);
	}
	
    public static void main( String[] args ) throws Throwable {    	
    	System.out.println("hello ..");
   	
    	split();
//    	String lightStatusSourceDir = "/home/twtuser/lightstatuses-removed-subset";
//    	DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
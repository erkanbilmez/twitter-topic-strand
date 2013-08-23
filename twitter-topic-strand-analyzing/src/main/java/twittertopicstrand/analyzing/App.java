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
		
	public static void convert() throws IOException{
    	String src = "/home/twtuser/reduced-tweets";
    	String dest = "/home/twtuser/lightstatuses";
    	String finalDest = "/home/twtuser/lightstatuses-removed";
    	
    	BilkentReducedToLightStatusConverter.convert(src, dest, true);
    	LightStatusSource lsource = new LightStatusSource(dest);
    	
    	String[] hashTags = HashtagSelector.getHashTags(lsource.getAll());
    	
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
	
    public static void main( String[] args ) throws Throwable {    	
    	System.out.println("hello ..");
    	
    	convert();
    	String lightStatusSourceDir = "/home/twtuser/lightstatuses-removed";
        DataAnalyzer.analyze(lightStatusSourceDir);
    	
    	System.out.println("bye .. ");
    }
}
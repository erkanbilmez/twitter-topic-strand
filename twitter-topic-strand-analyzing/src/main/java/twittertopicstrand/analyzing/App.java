package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import twittertopicstrand.util.MapOperations;

public class App {
	
	public static void convert() throws IOException {
    	//String src = "/home/twtuser/reduced-tweets";
    	String dest = "/home/mll2/Desktop/lightTweets";
    	//String finalDest = "/home/twtuser/lightstatuses-removed-splitted";    	
    	
    	//BilkentReducedToLightStatusConverter.convert(src, dest, true);
    	
    	String[] hashTags = HashtagSelector.getHashTags(dest);
    	System.out.println(Arrays.toString(hashTags));
    	
    	//HashTagSplitter.convert(dest, finalDest, hashTags);
	}
	
    public static void main( String[] args ) throws Throwable {    	
    	    	
    	System.out.println("hello ..");
    	    	
    	TFIDFAnalyzer analyzer = new TFIDFAnalyzer();
    	analyzer.analyze();
    	
//    	String src = "/home/mll2/Desktop/lightTweets-splitted-topics";
//    	DataAnalyzer.analyze(src);
    	
    	System.out.println("bye .. ");
    }    
}
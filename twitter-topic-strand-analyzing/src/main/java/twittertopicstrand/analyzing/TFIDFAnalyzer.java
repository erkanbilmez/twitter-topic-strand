package twittertopicstrand.analyzing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import twittertopicstrand.util.MapOperations;

public class TFIDFAnalyzer {
	
	public static void analyze() throws IOException{	
		TFIDFParser dp = new TFIDFParser();
		//dp.parseFiles("/home/mll2/Desktop/lightTweets-splitted-topics-onlyText-purified-subset");
		
		dp.parseFiles("/home/mll2/Desktop/deneme");
		
		HashMap<String, HashMap<String, Double>> result = dp.tfIdfCalculator(); // calculate // tfidf
	
		int i = 0;
		int n = 5;
		HashMap<String, Double> rVal = new HashMap<String, Double>();
		
		String resultText = "";
		
		for (Entry<String, HashMap<String, Double>> words : result.entrySet()) {
			
			Map<String, Double> sorted = MapOperations.sortMapByValue(words.getValue());
			
			i=0;
			
			resultText += words.getKey() + " --> ";
			for (Map.Entry<String, Double> entry : sorted.entrySet()) {
				
				
				rVal.put(entry.getKey(), entry.getValue());
				i++;
				
				if(i+1 > n) {
					break;
				}
				
				resultText += "{" + entry.getKey() + ": " + entry.getValue() + "}";
			}
			
			resultText += "\n";
			
			rVal.clear();
		}
		
		System.out.println(resultText);
	}

}

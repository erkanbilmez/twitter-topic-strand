package twittertopicstrand.analyzing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.MailSender;
import twittertopicstrand.util.MapOperations;

public class TFIDFAnalyzer {
	
	private HashMap<String, HashMap<String, Integer>> words = new HashMap<String, HashMap<String, Integer>>();
	private HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();	
	
	public void parse(String src) throws IOException {
		String[] files = FileOperations.getFiles(src);
		
		String allText = "";
		String line;
		String[] tokens;
		
		for(int i=0;i<files.length;i++){
			System.out.println(files[i]);
			
			String currentFile = FileOperations.getOnlyFileName( files[i] );
			words.put(currentFile, new HashMap<String, Integer>());			
			
			HashMap<String, Integer> currentHashMap = words.get(currentFile);

			BufferedReader br = new BufferedReader(new FileReader(files[i]));
	        while ((line = br.readLine()) != null) {
	        	allText += line + " ";
	        }
	        br.close();
	        
	        tokens = allText.split(" ");
        	for(String token: tokens){
        		int count = currentHashMap.containsKey(token) ? currentHashMap.get(token) : 0;
        		currentHashMap.put(token, count+1);
        	}
        	
        	Map<String, Integer> sortedMap = MapOperations.sortMapByValue(currentHashMap);
        	HashMap<String,Integer> firstNElements = MapOperations.getFirstNElements(sortedMap, 200);
        	
        	int sum = 0;
        	for(Entry<String, Integer> item: firstNElements.entrySet()){
        		sum += item.getValue();
        	}
        	wordCounts.put(currentFile, sum);        	
        	words.put(currentFile, firstNElements);
		}
	}
	
	public HashMap<String, HashMap<String, Double>> calculate() {
		
		HashMap<String, HashMap<String, Double>> rVal = new HashMap<String, HashMap<String, Double>> ();
		
		double tf; // term frequency
		double idf; // inverse document frequency
		double tfidf; // term requency inverse document frequency
		
		for(Entry<String, HashMap<String, Integer>> file: words.entrySet()){
			
			String fileName = file.getKey();
			
			rVal.put(fileName, new HashMap<String, Double>());
			
			HashMap<String, Integer> wordList = file.getValue();
			
			for(Entry<String, Integer> item: wordList.entrySet()){
				String word = item.getKey();
				Integer count = item.getValue();
				
				tf = count * 1.0 / wordCounts.get(fileName);
				idf = Math.log10(wordCounts.size() * 1.0 / howManyWords(word));	
				
				tfidf = tf * idf;
				rVal.get(fileName).put(word, tfidf);				
			}
		} 
		
		return rVal;
	}	
	
	public int howManyWords(String word){
		int rVal = 0;
		
		for(Entry<String, HashMap<String, Integer>> file: words.entrySet()){
			HashMap<String, Integer> wordList = file.getValue();
			
			if(wordList.containsKey(word))
				rVal ++;
		} 
		
		return rVal;
	}
		
	public void analyze() throws IOException {
		parse("/home/mll2/Desktop/lightTweets-splitted-topics-onlyText-purified");
		HashMap<String, HashMap<String, Double>> result = calculate();
		
		for(Entry<String, HashMap<String, Double>> item: result.entrySet()) {		
			
			Map<String, Double> sorted = MapOperations.sortMapByValue(item.getValue());
			HashMap<String, Double> firstNElements = MapOperations.getFirstNElements(sorted, 50);			
			System.out.println(item.getKey() + " --> " + firstNElements);			
		}
	}
}

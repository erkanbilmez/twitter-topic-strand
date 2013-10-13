package twittertopicstrand.analyzing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import twittertopicstrand.util.FileOperations;

public class TFIDFParser {
	
	// This variable will hold all terms of each document in an array.
	private HashMap<String, String[]> termsDocsArray = new HashMap<String, String[]>();
	private HashSet<String> allTerms = new HashSet<>();
	private HashMap<String, HashMap<String, Double>> tfidfresult = new HashMap<>();
	
	public void parseFiles(String filePath) throws IOException {
		File[] allfiles = new File(filePath).listFiles();
		
		BufferedReader in = null;
		for (File f : allfiles) {			
			in = new BufferedReader(new FileReader(f));
			StringBuilder sb = new StringBuilder();
			String s = null;
			
			while ((s = in.readLine()) != null) {					
				sb.append(s).append(" ");				
			}

			String result = sb.toString();
			String[] tokenizedTerms = result.split(" "); // to															

			for (String term : tokenizedTerms) {
				if (term.length() != 0)
					allTerms.add(term);				
			}

			termsDocsArray.put(f.getName(), tokenizedTerms);
			System.out.println(f.getName());
			in.close();			
		}
	}
	
	public HashMap<String, HashMap<String, Double>> tfIdfCalculator() {
		double tf; // term frequency
		double idf; // inverse document frequency
		double tfidf; // term requency inverse document frequency
		for (Entry<String, String[]> docTermsArray : termsDocsArray.entrySet()) {
			for (String terms : allTerms) {
				tf = TFIDFCalculator.tfCalculator(docTermsArray.getValue(), terms);
				idf = TFIDFCalculator.idfCalculator(termsDocsArray.values(), terms);
				tfidf = tf * idf;
				if (tfidf > 0) {	
					if (!tfidfresult.containsKey(docTermsArray.getKey())) {
						HashMap<String, Double> wordtfidf = new HashMap<String, Double>();
						wordtfidf.put(terms, tfidf);
						tfidfresult.put(docTermsArray.getKey(), wordtfidf);
					} else {
						tfidfresult.get(docTermsArray.getKey()).put(terms,
								tfidf);
					}
				}
			}
		}
		return tfidfresult;
	}	
	
	public static String purifyOneLine(String input){
		
		input = input.toLowerCase();
		
		String rVal = "";
		String[] parts = input.split(" ");
		String[] invalids = new String[] { "@", "#", "http", "ftp" };
		
		boolean isValid = true;
		
		for(int i=0;i<parts.length;i++){
		
			isValid = true;
			
			for(int j=0;j<invalids.length;j++) { 
				if(parts[i].contains(invalids[j]) || parts[i].length() < 3){
					isValid = false;
					break;
				}
			}
			
			if(isValid){
				rVal += parts[i] + " ";
			}
		}
		
		String temp = rVal.startsWith("rt") ? rVal.substring(3, rVal.length()) : rVal;

		rVal = "";
		
		for(int i=0;i<temp.length();i++){
			if(Character.isLetter(temp.charAt(i)) || temp.charAt(i) == ' ') {
				rVal += temp.charAt(i);
			}else{
				rVal += " ";
			}
		}				

		rVal = rVal.replaceAll(" rt ", " ");
		rVal = rVal.replaceAll(" rt$", " ");
		rVal = rVal.replaceAll("^rt ", " ");
		rVal = rVal.replaceAll("\\s+", " ");
		rVal = rVal.trim();
	
		return rVal;		
	}
	
	public static void purifyAll(String src, String dest) throws IOException{
		String[] files = FileOperations.getFiles(src);
		
		for(int i=0;i<files.length;i++){
			String srcFileName = FileOperations.getOnlyFileName( files[i] );
			String destFileName = dest + File.separator + srcFileName;
			 
	        BufferedReader br = new BufferedReader(new FileReader(files[i]));
	        String line;
		        
	        while ((line = br.readLine()) != null) {
	        	String result = purifyOneLine(line);
	        	if(result.length()>2){
	        		FileOperations.addLine(result, destFileName);
	        	}
	        }
	        br.close();
		}		
	}
}

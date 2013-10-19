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
import java.util.Map;
import java.util.Map.Entry;

import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.MapOperations;

public class TFIDFParser {
	
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

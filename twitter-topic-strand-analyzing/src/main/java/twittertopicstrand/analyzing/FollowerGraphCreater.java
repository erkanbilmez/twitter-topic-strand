package twittertopicstrand.analyzing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import twittertopicstrand.util.FileOperations;

public class FollowerGraphCreater {
	
	public static HashMap<Long, HashSet<Long>> graph;
	
	public static void reduceGraphFile(String inputFile, String outputFile, String masterTxt) throws NumberFormatException, IOException{
		String line;
	
		BufferedReader br1 = new BufferedReader( new FileReader(masterTxt) );
	    HashSet<Long> followedUsers = new HashSet<Long>();
	    
		while ((line = br1.readLine()) != null) {
			long user = Long.valueOf(line);
			followedUsers.add(user);
		}		
		
        BufferedReader br2 = new BufferedReader( new FileReader(inputFile) );
        int i = 0;
        while ((line = br2.readLine()) != null) {
        	
        	String[] parts = line.split(",");
        
        	long follower = Long.valueOf(parts[1]);
        	long followee = Long.valueOf(parts[2]);
        	
        	if(followedUsers.contains(follower)){
        		FileOperations.addLine(line, outputFile);
        	}
        	i++;
        	if(i%100000==0){
        		System.out.println(i);
        	}
        }        	
	}
	
	public static HashMap<Long, HashSet<Long>> create(String fileName) throws IOException{		

		if(graph != null) {
			return graph;
		}
		
		System.out.println("Graph generation started.");
		
		HashMap<Long, HashSet<Long>> rVal = new HashMap<Long, HashSet<Long>>();
		
        BufferedReader br = new BufferedReader( new FileReader(fileName) );
        String line;
                
        while ((line = br.readLine()) != null) {
        	
        	String[] parts = line.split(",");
        	
        	long follower = Long.valueOf(parts[1]);
        	long followee = Long.valueOf(parts[2]);
        	
        	if( !rVal.containsKey(follower) ) {
        		rVal.put(follower, new HashSet<Long>());
        	}
        	       	
        	rVal.get(follower).add(followee);
        }
        
        br.close();
                
        graph = rVal;
        
        System.out.println("Graph generation finished.");
        
		return graph;				
	}
	
	
}

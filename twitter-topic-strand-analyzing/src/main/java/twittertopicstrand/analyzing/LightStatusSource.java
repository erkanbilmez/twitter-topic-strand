package twittertopicstrand.analyzing;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import twitter4j.LightStatus;
import twittertopicstrand.util.FileOperations;

public class LightStatusSource {

	String folderPath;
	String[] files;
	int currentIndex;
	
	public LightStatusSource(String folderPath){
		this.folderPath = folderPath;
		this.files = FileOperations.getFiles(folderPath);
	}
	
	public String getCurrentFileName(){
		return this.files[this.currentIndex];
	}
	
	public boolean iterate() {
		if(currentIndex == files.length - 1){
			return false;
		}
		currentIndex ++;
		return true;		
	}
	
	public LightStatus[] getChunk() throws IOException {
		LightStatus[] rVal = null;
		
		List<LightStatus> tempList = new ArrayList<LightStatus>();
		
        BufferedReader br = new BufferedReader(new FileReader(this.files[this.currentIndex]));
        
        String line;
        LightStatus temp;
        
        while ((line = br.readLine()) != null) {			
			temp = LightStatus.fromJSONString(line);			
			tempList.add(temp);		
        }
        br.close();
        
		rVal = tempList.toArray(new LightStatus[tempList.size()]);
		
		return rVal;
	}
	
	public void refresh() {
		currentIndex = 0;				
	}	
	
	public LightStatus[] getAll() throws IOException{
		LightStatus[] rVal = null;
		
		List<LightStatus> tempList = new ArrayList<LightStatus>();
		
		for(int i=0;i<this.files.length;i++){
	        BufferedReader br = new BufferedReader(new FileReader(this.files[this.currentIndex]));
	        
	        String line;
	        LightStatus temp;
	        
	        while ((line = br.readLine()) != null) {			
				temp = LightStatus.fromJSONString(line);			
				tempList.add(temp);		
	        }
	        br.close();
		}
        
		rVal = tempList.toArray(new LightStatus[tempList.size()]);
		
		return rVal;		
	}
}

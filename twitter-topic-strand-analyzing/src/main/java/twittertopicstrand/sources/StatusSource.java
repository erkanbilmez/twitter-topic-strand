package twittertopicstrand.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.StatusFactory;
import twittertopicstrand.util.FileOperations;

public class StatusSource {
	String folderPath;
	String[] files;
	int currentIndex;
	
	public StatusSource(String folderPath){
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
	
	public Status[] getChunk() throws IOException {
		Status[] rVal = null;
		
		List<Status> tempList = new ArrayList<Status>();
		
        BufferedReader br = new BufferedReader(new FileReader(this.files[this.currentIndex]));
        String line;
        Status temp = null;
        
        while ((line = br.readLine()) != null) {
			temp = StatusFactory.fromString(line);
			tempList.add(temp);		
        }
        br.close();
        
		rVal = tempList.toArray(new Status[tempList.size()]);
		
		return rVal;
	}
	
	public void refresh() {
		currentIndex = 0;				
	}	
	
	public static void convertLightStatusSource(String fromDir, String toDir, boolean onlyHashTaggeds) throws IOException {
		StatusSource src = new StatusSource(fromDir);
		
		do{
			Status[] chunk = src.getChunk();
			String filePath = src.getCurrentFileName();
			String fileName = FileOperations.getOnlyFileName(filePath);
			String outputFileName = toDir + File.separator + fileName;
			
			for(int i=0;i<chunk.length;i++){
				HashtagEntity[] hashtagEntities = chunk[i].getHashtagEntities();
				if(onlyHashTaggeds) {
					if(hashtagEntities != null && hashtagEntities.length > 0){
						FileOperations.addLine(chunk[i].toLightStatus().toJSONString(), outputFileName);
					}	
				}
				else{
					FileOperations.addLine(chunk[i].toLightStatus().toJSONString(), outputFileName);
				}
				
			}
			
		}while(src.iterate());
	}
}

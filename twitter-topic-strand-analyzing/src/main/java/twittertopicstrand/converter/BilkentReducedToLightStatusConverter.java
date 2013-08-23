package twittertopicstrand.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import twitter4j.LightStatus;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.HourOperations;

public class BilkentReducedToLightStatusConverter {
	

	public static void convertFile(String sourceFile, String destFile, boolean onlyHashTaggeds) throws IOException{
		
		List<String> lines = FileOperations.readFile(sourceFile);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destFile) , true));
	        
		for(int i=0;i<lines.size();i++) {
			String line = lines.get(i);
			
			try{
				LightStatus ls = processLine(line, sourceFile);
				if(ls.hashTags.length > 0){
					bw.write(ls.toJSONString());
	        		bw.newLine();
				}
			}
			catch(Exception ex){
				System.out.println("one line couldn't convert.");
			}
		}
	    bw.close();
	}
	
	private static LightStatus processLine(String line, String sourceFile){
		String[] parts = line.split("\\|");
		
		LightStatus ls = new LightStatus();
		
		String dtString = sourceFile.substring(sourceFile.length() - 12, sourceFile.length() - 4) + 
				"-" + parts[0];
		
		ls.createdAt = HourOperations.getDateTime(dtString).toDate();
		ls.id = Long.valueOf(parts[1]);
		ls.userId = Long.valueOf(parts[2]);
		ls.retweetedStatusId = parts[3].isEmpty() ? -1l : Long.valueOf(parts[3]);
		ls.retweetedStatusUserId = parts[4].isEmpty() ? -1l : Long.valueOf(parts[4]);
		ls.hashTags = parts[7].isEmpty() ? new String[] {} : parts[7].split(",");
		
		return ls;
	}
	
	public static void convert(String sourceDir, String destDir, boolean onlyHashTaggeds) throws IOException{
		String[] files = FileOperations.getFiles(sourceDir);
		
		for(int i=0;i<files.length;i++){
			String sourceFile = files[i];
			String destFile = destDir + File.separator + FileOperations.getOnlyFileName(sourceFile);
			
			if(FileOperations.fileExists(destFile)){
				System.out.println("skipping, already done: " + sourceFile);
				continue;
			}
			
			System.out.println(sourceFile);
			
			String tempFile = destDir + File.separator + UUID.randomUUID();
			
			convertFile(sourceFile, tempFile, onlyHashTaggeds);
			FileOperations.sortFileLightStatus(tempFile, destFile);
			
			File f = new File(tempFile);
			f.delete();
		}
	}
	

}

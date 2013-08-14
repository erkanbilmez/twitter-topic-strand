package twittertopicstrand.converter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import twitter4j.LightStatus;
import twittertopicstrand.util.FileOperations;

public class BilkentReducedToLightStatusConverter {
	
	public static void convertFile(String sourceFile, String destFile) throws IOException{
		
		List<String> lines = FileOperations.readFile(sourceFile);
		
		for(int i=0;i<lines.size();i++) {
			String line = lines.get(i);
			String[] parts = line.split("\\|");
			
			LightStatus ls = new LightStatus();
			
			// ls.createdAt = burada kaldim...
			ls.id = Long.valueOf(parts[1]);
			ls.userId = Long.valueOf(parts[2]);
			ls.retweetedStatusId = Long.valueOf(parts[3]);
			ls.retweetedStatusUserId = Long.valueOf(parts[4]);
			ls.hashTags = parts[7].split(",");
		
		}
		
	}
	
	public static void convert(String sourceDir, String destDir) throws IOException{
		String[] files = FileOperations.getFiles(sourceDir);
		
		for(int i=0;i<files.length;i++){
			String sourceFile = files[i];
			String destFile = destDir + File.separator + FileOperations.getOnlyFileName(sourceFile);
			
			convertFile(sourceFile, destFile);
		}
	}
}

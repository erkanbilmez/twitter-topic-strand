package twittertopicstrand.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import twitter4j.LightStatus;
import twittertopicstrand.util.FileOperations;

public class HashTagRemover {
	
	public static boolean arrayContaionsAnyOfArray(String[] arr1, String[] arr2){
		for(int i=0;i<arr1.length;i++){
			for(int j=0;j<arr2.length;j++){
				if(arr1[i].equals(arr2[j])){
					return true;
				}
			}
		}
		return false;
	}
	
	public static void convertFile(String sourceFile, String destFile, String[] hashTags) throws IOException{
		List<String> lines = FileOperations.readFile(sourceFile);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(destFile) , true));
	        
		for(int i=0;i<lines.size();i++) {
			String line = lines.get(i);
			try{
				LightStatus ls = LightStatus.fromJSONString(line);
				if(ls.hashTags.length > 0){
					if(arrayContaionsAnyOfArray(ls.hashTags, hashTags)){
						bw.write(ls.toJSONString());
						bw.newLine();
					}
				}
			}
			catch(Exception ex){
				System.out.println("one line couldn't convert.");
			}
		}
	    bw.close();
	}
	
	public static void convert(String sourceDir, String destDir, String[] hashTags) throws IOException{
		String[] files = FileOperations.getFiles(sourceDir);
		
		for(int i=0;i<files.length;i++){
			String sourceFile = files[i];
			
			System.out.println(sourceFile);
			String destFile = destDir + File.separator + FileOperations.getOnlyFileName(sourceFile);
			
			String tempFile = destDir + File.separator + UUID.randomUUID();
			
			convertFile(sourceFile, tempFile, hashTags);
			FileOperations.sortFileLightStatus(tempFile, destFile);
			
			File f = new File(tempFile);
			f.delete();
		}
	}
}

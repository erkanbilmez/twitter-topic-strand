package twittertopicstrand.analyzing;

import java.io.File;
import java.io.IOException;

import twitter4j.LightStatus;
import twittertopicstrand.converter.HashTagRemover;
import twittertopicstrand.sources.LightStatusSource;
import twittertopicstrand.util.FileOperations;

public class HashTagSplitter {

	public static void convert(String src, String dest, String[] hashTags) throws IOException{
		LightStatusSource ls = new LightStatusSource(src);
		
		do{
			LightStatus[] chunk = ls.getChunk();
			for(int i=0;i<chunk.length;i++){
				LightStatus current =  chunk[i];
				
				String[] intersection = HashTagRemover.getIntersection(current.hashTags, hashTags);
				
				for(int j=0;j<intersection.length;j++){
					String destFile = dest + File.separator + intersection[j] + ".txt";
					FileOperations.addLine(current.toJSONString(), destFile);
				}
			}
		}while(ls.iterate());
	}
}

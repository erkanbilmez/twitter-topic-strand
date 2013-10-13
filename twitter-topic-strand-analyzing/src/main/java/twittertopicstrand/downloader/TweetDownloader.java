package twittertopicstrand.downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twittertopicstrand.util.FileOperations;

public class TweetDownloader {
	public static void download() throws IOException{	
		List<String> lines = FileOperations.readFile("/home/mll2/Desktop/xxx.txt");
		String temp = lines.get(0);
		
		ArrayList<String> results = new ArrayList<String>();
		
		for(int i=1;i<lines.size();i++){
			String line = lines.get(i);
			String subSeq = line.substring(39, 62);
			subSeq = "wget " + temp + subSeq;
			System.out.println(subSeq);
			
			results.add(subSeq);
		}
		
		FileOperations.writeFile(results, "/home/mll2/Desktop/downloadScript.sh");
	}
}

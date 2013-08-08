package twittertopicstrand.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import twittertopicstrand.util.FileOperations;

public class ReduceOperations {
	FileWriter fileWriter = null;
	BufferedWriter bufferedWriter = null;
	TweetParser response = null;
	List<String> errorList = null;
	int success = 0;
	int failed = 0;
	int tweetcount = 0;
	
	boolean ToStatus; //If true converts to status else converts to lightstatus
	
	public ReduceOperations (boolean ToStatus) {
		this.ToStatus = ToStatus;
	}
	
	public void runAllFolderFiles (String sourceFolder, String destinationFolder)
	{
		success = failed = tweetcount = 0;
		
		String[] sourceFiles = FileOperations.getFiles(sourceFolder);
		
		reduceTweetData(sourceFiles, destinationFolder);
	}
	/*
	public void runDirectoryFile (String filepath, String destinationFolder)
	{
		success = failed = tweetcount = 0;
		
		List<String>files = FileOperation.ReadFile(filepath);
		String[] sourceFiles = new String[files.size()];
		for (int i=0; i<files.size();i++)
			sourceFiles[i] = files.get(i);
		files.clear();
		
		reduceTweetData(sourceFiles, destinationFolder);
	}*/
	
	public void reduceTweetData (String[]sourceFiles, String destinationFolder)
	{
		errorList = new ArrayList<String>();
		
		File folder = new File(destinationFolder);
		if (!folder.exists())
			folder.mkdir();
		
		String line = null;
		String multiline = "";
		String tweetline = "";
		
		InputStreamReader istreamReader = null;
		BufferedReader bufferedReader = null;
		
		try {
			for (int i=0; i<sourceFiles.length; i++)
			{
				System.out.println("Files: " +String.valueOf(i+1) + " / " + String.valueOf(sourceFiles.length));
				try {
					istreamReader = new InputStreamReader(new FileInputStream(sourceFiles[i]), "UTF8");
			        bufferedReader =  new BufferedReader(istreamReader);
			        
			        int indexTweet = 0;
			        while((line = bufferedReader.readLine()) != null)
			        {
			        	try 
			        	{
				        	multiline += line;
				        	
				        	indexTweet = multiline.indexOf("}StatusJSONImpl{createdAt=");
				        	if (indexTweet == -1)
				        		continue;
				        	
				        	tweetline = multiline.substring(0, indexTweet);
				        	multiline = multiline.substring(indexTweet+1);
				        	
				        	if (WriteTweet(tweetline, destinationFolder))
				        		success++;
				        	else
				        		failed++;
			        	}
			        	catch (Exception ex)
			        	{
			        		failed++;
			        	}
			        }
			        
			        bufferedReader.close();
			        istreamReader.close();
				}
				catch (Exception ex)
				{
					failed ++;
				}
			}
			
			System.out.println("Results:");
			System.out.println("Successful: " +String.valueOf(success) + " Tweets");
			System.out.println("Failed: " +String.valueOf(failed) + " Tweets");
			
			if (errorList.size()==0)
				return;
			
			System.out.println("\nErrors:");
			for (int i=0; i<errorList.size(); i++)
				System.out.println(errorList.get(i));
		}
		catch (Exception ex)
		{
			System.out.println("ERROR:" + ex.getMessage());
		}
	}
	
	String lastDateData = "";
	DateConverter dc = null;
	public boolean WriteTweet(String tweettext, String dest_folder)
	{
		try {
			response = new TweetParser();
			if (dc == null)
				dc = new DateConverter();
	    	response.ParseJSON(tweettext, dc);
	    	
	    	if (response.hasError)
	    	{
	    		errorList.add(response.errorMessage);
	    		return false;
	    	}
	    	
	    	if (ToStatus)
	    		tweettext = response.tweet.ToStatusString();
	    	else
	    		tweettext = response.tweet.ToLightString(dc);
	    	
	    	tweetcount++; 
	    	String newDateData = response.tweet.dateStr;
	    	    	
	    	
	    	if (!newDateData.equals(lastDateData))
	    	{
	    		if (bufferedWriter != null)
	    			bufferedWriter.close();
	    		if (fileWriter != null)
	    			fileWriter.close();
	    		
	    		String path = dest_folder + File.separator + "tweeets_" + response.tweet.dateStr + ".txt";
	    		System.out.println("New Destination File: " + path);
	    		
		        File f = new File(path);
		        if (!f.exists())
		        	f.createNewFile();
		        
		        fileWriter = new FileWriter(path, true);
		        bufferedWriter = new BufferedWriter(fileWriter);
	    	}
	    	lastDateData = newDateData;
	    	
	        bufferedWriter.write(tweettext);
	        bufferedWriter.newLine();
	        
	        return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
}
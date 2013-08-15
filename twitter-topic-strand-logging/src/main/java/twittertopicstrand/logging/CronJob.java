package twittertopicstrand.logging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import twitter4j.Status;
import twitter4j.StatusFactory;
import twitter4j.internal.json.StatusJSONImpl;
import twittertopicstrand.util.FileOperations;
import twittertopicstrand.util.MailSender;

public class CronJob implements Runnable {
	
	String sourceFolderPath;
	String destFolderPath;
	
	public CronJob(String sourceFolderPath, String destFolderPath) {
		this.sourceFolderPath = sourceFolderPath;
		this.destFolderPath = destFolderPath;		
	}
	
	public static DateTime getFileDate(String fileName) {
		DateTime rVal;
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yy-MM-dd-HH");
		
		int index = fileName.lastIndexOf(File.separator);
		String s = fileName.substring(index+1, index + 14);
		
		rVal = fmt.parseDateTime(s);
		
		return rVal;
	}
	
	public static void createZipFile(String inputFileName, String outputFileName){
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {            
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(
                                 new OutputStreamWriter(
                                     new GZIPOutputStream(new FileOutputStream(outputFileName))
                                 ));

            //Construct the BufferedReader object
            bufferedReader = new BufferedReader(new FileReader(inputFileName));
            
            String line = null;
            
            // from the input file to the GZIP output file
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //Close the BufferedWrter
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            //Close the BufferedReader
            if (bufferedReader != null ){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public static void processThisDate(String sourceFolderPath, String destFolderPath, DateTime dt) throws IOException{				
		String[] files = FileOperations.getFiles(sourceFolderPath);
		
		List<String> tempFiles = new ArrayList<String>();
	
		for(int i=0;i<files.length;i++){
			String currentFile = files[i];
			
			DateTime fileDate = getFileDate(currentFile);
			
			if(fileDate.isEqual(dt)) {				
				tempFiles.add(currentFile);
			}
		}
		
		String unSortedDestFile = destFolderPath + File.separator + dt.toString("yy-MM-dd-HH") + "-unsorted.txt";
		String destFile = destFolderPath + File.separator + dt.toString("yy-MM-dd-HH") + ".txt";
				
		String[] tempFilesArray = tempFiles.toArray(new String[tempFiles.size()]);
		
		FileOperations.combineFiles(tempFilesArray, unSortedDestFile);
		
		for(int i=0;i<tempFilesArray.length;i++) {
			File f = new File(tempFilesArray[i]);
			f.delete();
		}
		
		int numTweets = FileOperations.sortFileStatus(unSortedDestFile, destFile);
		
		try{
			String to = "sehir.tweet.logging@gmail.com";
			String subject = "hourly report";
			String body = String.valueOf(numTweets) + " tweets in " + dt.toString("yy-MM-dd-HH");
			
			MailSender.send(to, subject, body);
		}catch(Exception ex){
			System.out.println("problem with sending e-mail..");
		}
		
		File f1 = new File(unSortedDestFile);
		f1.delete();
		
		createZipFile(destFile, destFile + ".gz");	
		
		File f2 = new File(destFile);
		f2.delete();
	}
	
	public void tick() {
		String[] files = FileOperations.getFiles(this.sourceFolderPath);
		
		for(int i=0;i<files.length;i++) {
			String currentFile = files[i];			

			DateTime fileDate = getFileDate(currentFile);
			DateTime now = new DateTime();
			
			Duration diff = new Duration(fileDate, now);
			
			if(diff.getStandardHours() > 1) {
				try {
					processThisDate(this.sourceFolderPath, this.destFolderPath, fileDate);
				} catch (IOException e) { System.out.println(e.getMessage()); }
				
				i=0;
				files = FileOperations.getFiles(this.sourceFolderPath);
			}
		}			
	}
	
	public void run() {
		
		while(true) {
			this.tick();
			try {
				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException e) { System.out.println(e.getMessage()); }
		}
		
	}
}
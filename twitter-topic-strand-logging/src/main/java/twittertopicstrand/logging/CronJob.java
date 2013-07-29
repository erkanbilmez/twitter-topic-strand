package twittertopicstrand.logging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import twitter4j.Status;
import twitter4j.StatusFactory;
import twitter4j.internal.json.StatusJSONImpl;
import twittertopicstrand.util.FileOperations;

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
	
	public static void sortFile(String sourceFilePath, String destFilePath) throws IOException {
		
		Map<Long, String> myMap = new HashMap<Long,String>();
		
		List<String> lines = FileOperations.readFile(sourceFilePath);
		List<Status> statuses = new ArrayList<Status>(lines.size());
		for(int i=0;i<lines.size();i++){
			String currentLine = lines.get(i);
			Status status = StatusFactory.fromString(currentLine); 
			statuses.add(status);
		}
		Collections.sort(statuses);
		
		for(int i=0;i<statuses.size();i++){
			FileOperations.addLine( statuses.get(i).toFriendlyString(), destFilePath);
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
		
		sortFile(unSortedDestFile, destFile);
		
		File f1 = new File(unSortedDestFile);
		f1.delete();
		
		createZipFile(destFile, destFile + ".gz");	
		
		File f2 = new File(destFile);
		f2.delete();
	}
	
	public void tick() {
		String[] files = FileOperations.getFiles(this.sourceFolderPath);
		
		DateTime now = new DateTime().withMinuteOfHour(0).withSecondOfMinute(0).minusHours(2); // for safety
				
		for(int i=0;i<files.length;i++) {
			String currentFile = files[i];
			
			DateTime fileDate = getFileDate(currentFile);
			
			if(fileDate.isBefore(now)){
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
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

import org.joda.time.LocalDate;

import twitter4j.Status;
import twitter4j.internal.json.StatusFactory;
import twitter4j.internal.json.StatusJSONImpl;
import twittertopicstrand.util.FileOperations;

public class CronJob implements Runnable {
	
	String sourceFolderPath;
	String destFolderPath;
	
	public CronJob(String sourceFolderPath, String destFolderPath) {
		this.sourceFolderPath = sourceFolderPath;
		this.destFolderPath = destFolderPath;		
	}
	
	public static LocalDate getLocalDate(String fileName) {
		LocalDate rVal;
		
		int index = fileName.lastIndexOf(File.separator);
		String s = fileName.substring(index+1, index + 11);
		
		rVal = new LocalDate(s);
		
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
			Status status = StatusFactory.fromString(lines.get(i)); 
			statuses.add(status);
			myMap.put(status.getId(), lines.get(i));
		}
		Collections.sort(statuses);
		
		for(int i=0;i<statuses.size();i++){
			FileOperations.addLine( myMap.get(statuses.get(i).getId()), destFilePath);
		}
	}
	
	private static void processThisDate(String sourceFolderPath, String destFolderPath, LocalDate dt) throws IOException{				
		String[] files = FileOperations.getFiles(sourceFolderPath);
		
		List<String> tempFiles = new ArrayList<String>();
	
		for(int i=0;i<files.length;i++){
			String currentFile = files[i];
			LocalDate localDate = getLocalDate(currentFile);
			
			if(localDate.isEqual(dt)) {				
				tempFiles.add(currentFile);
			}
		}
		
		String unSortedDestFile = destFolderPath + File.separator + dt.toString() + "-unsorted.txt";
		String destFile = destFolderPath + File.separator + dt.toString() + ".txt";
				
		String[] tempFilesArray = tempFiles.toArray(new String[tempFiles.size()]);
		
		FileOperations.combineFiles(tempFilesArray, unSortedDestFile);
		
		for(int i=0;i<tempFilesArray.length;i++) {
			File f = new File(tempFilesArray[i]);
			System.out.println(tempFilesArray[i]);
			f.delete();
		}
		
		sortFile(unSortedDestFile, destFile);
		
		createZipFile(destFile, destFile + ".gz");	
		
		File f = new File(destFile);
		f.delete();
	}
	
	public void tick() {
		String[] files = FileOperations.getFiles(this.sourceFolderPath);
		
		for(int i=0;i<files.length;i++) {
			LocalDate now = new LocalDate();
			
			String currentFile = files[i];
			LocalDate localDate = getLocalDate(currentFile);
		
			if(localDate.isBefore(now)){
				try {
					processThisDate(this.sourceFolderPath, this.destFolderPath, localDate);
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
				Thread.sleep(1000 * 60 * 60 * 2);
			} catch (InterruptedException e) { System.out.println(e.getMessage()); }
		}
		
	}
}
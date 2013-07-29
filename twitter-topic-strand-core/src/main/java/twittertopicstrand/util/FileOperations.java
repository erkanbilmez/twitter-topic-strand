package twittertopicstrand.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {

    public static String getOnlyFileName(String path){
        return path.substring(path.lastIndexOf("/") + 1);
    }
    
    public static String[] getFiles(String path){
        File folder = new File (path);
        File[] files = folder.listFiles();
        String [] absPaths = new String[files.length];

        for (int i=0; i<files.length; i++)
            absPaths[i] = files[i].getAbsolutePath();

        return absPaths;
    }
    
    public static void combineFiles(String[] files, String outputFile) throws IOException{
    	
    	File f = new File(outputFile);
       	if( f.exists() ) {
       		f.delete();
    	}
    	    	
    	for(int i=0;i<files.length;i++){
    		String currentFile = files[i];
    		
    		List<String> lines = FileOperations.readFile(currentFile);
    		
    		for(int j=0;j<lines.size();j++) {    			
    			String currentLine = lines.get(j);
    			FileOperations.addLine(currentLine, outputFile);
    		}
    	}
    }
    
    public static List<String> readFile(String file) throws IOException {
        List<String> rVal = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        
        while ((line = br.readLine()) != null) {
            rVal.add(line);
        }
        br.close();

        return rVal;
    }

    public static void writeFile(List<String> lines, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), false));

            for (int i=0;i<lines.size();i++){
                bw.write(lines.get(i));
                bw.newLine();
            }

            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    

    public static void writeFile(String line, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), false));
            bw.write(line);
            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void addLine(String line, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path) , true));

            bw.write(line);
            bw.newLine();

            bw.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String readAllText(String filePath) throws IOException {
        String rVal = "";

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            rVal = sb.toString();
        } finally {
            br.close();
        }

        return rVal;
    }

    public static Boolean fileExists(String filePathString) {
        Boolean rVal = false;

        File f = new File(filePathString);
        rVal = f.exists();

        return rVal;
    }

    public static String getLastLine(String fileName) throws IOException {
        RandomAccessFile fileHandler = null;
        try {
            File file = new File(fileName)           ;
            fileHandler = new RandomAccessFile( file, "r" );
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );
                int readByte = fileHandler.readByte();

                if( readByte == 0xA ) {
                    if( filePointer == fileLength ) {
                        continue;
                    } else {
                        break;
                    }
                } else if( readByte == 0xD ) {
                    if( filePointer == fileLength - 1 ) {
                        continue;
                    } else {
                        break;
                    }
                }

                sb.append( ( char ) readByte );
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        }
        finally { fileHandler.close(); }
    }

}
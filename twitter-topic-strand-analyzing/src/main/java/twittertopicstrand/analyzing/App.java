package twittertopicstrand.analyzing;

import java.io.IOException;

import org.joda.time.DateTime;

import twitter4j.LightStatus;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException {
        
    	String sourceDirectory = "/home/mll2/Desktop/x";    	
    	
    	LightStatusSource lsSource = new LightStatusSource(sourceDirectory);
    	
    	do{
    		LightStatus[] chunk = lsSource.getChunk();
    		System.out.println(lsSource.getCurrentFileName());

    		System.out.println(chunk.length);
    		
    	}while(lsSource.iterate());
    	
    }
}
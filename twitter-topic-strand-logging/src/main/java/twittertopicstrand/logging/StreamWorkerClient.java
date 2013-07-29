package twittertopicstrand.logging;

import java.io.File;

import org.joda.time.DateTime;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.internal.json.StatusJSONImpl;
import twittertopicstrand.util.FileOperations;

public class StreamWorkerClient implements Runnable {	
	
    APIUser myUser;
    int threadID;
    long[] users;
    private String outputDir;    

    public StreamWorkerClient(APIUser usr, long[] users, String outputDir, int threadID) {
        this.myUser = usr;
        this.users = users;
        this.threadID = threadID;        
        this.outputDir = outputDir;
    }
    
    private String getFileName() {
    	String rVal = outputDir + File.separator;
    	
    	DateTime now = new DateTime();
    	
    	rVal += now.toString("yyyy-MM-dd-hh");
    	rVal += "-tID" +  String.format("%04d", this.threadID) + ".txt";
    	
    	return rVal;
    }

    private TwitterStream verifyTwitterStream() {
        TwitterStream rVal = null;
        boolean success = false;
        do {
            try{
                rVal = this.myUser.GetTwitterStream();
                success=true;
            }
            catch(Exception ex){
                success = false;
            }

            if(!success) {
                System.out.println("Thread " + threadID + " couldn't verified. Thread will sleep for 60 secs.");
            }

            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {

        }

        }while (success != true);

        return rVal;
    }

	@Override
	public void run() {
		System.out.println("Thread " + threadID + " started.");

        TwitterStream twitterStream = this.verifyTwitterStream();

        System.out.println("Thread " + threadID + " verified.");
        
        twitterStream.addListener(new StatusListener() {
			
			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onStatus(Status arg0) {				
				String fileName = getFileName();
				StatusJSONImpl s = (StatusJSONImpl)arg0;
				FileOperations.addLine(s.toFriendlyString(), fileName);
			}
			
			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub				
			}
		});
                
        twitterStream.filter(new FilterQuery(this.users));   
	}
}
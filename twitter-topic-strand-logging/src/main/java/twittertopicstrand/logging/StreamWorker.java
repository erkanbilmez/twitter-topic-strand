package twittertopicstrand.logging;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class StreamWorker {	
	
	 public static void startStreaming(long[] users, String apiUsersPath, String tempOutputPath, String finalOutputPath) throws ParserConfigurationException, IOException, SAXException {	    	
		
		 List<APIUser> apiUsers = APIUser.getUsers(apiUsersPath, -1);

		 ExecutorService executor = Executors.newCachedThreadPool();
		 
		 for(int i=0;i<apiUsers.size();i++) {
			 
			 APIUser user = apiUsers.get(i);
			 
			 long[] currentUsers = Arrays.copyOfRange(users, i * 5000, (i+1) * 5000);
			 
			 StreamWorkerClient client = new StreamWorkerClient(user, currentUsers, tempOutputPath, i);
			   
             executor.execute(client);		     
		 }
		 
		 CronJob job = new CronJob(tempOutputPath, finalOutputPath);
		 executor.execute(job);
		 
	 }    
	 
}
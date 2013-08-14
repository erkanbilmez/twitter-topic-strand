package twittertopicstrand.logging;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.ArrayUtils;
import org.xml.sax.SAXException;

import twitter4j.UserFactory;

public class StreamWorker {	
	
	public static List<Long> getUserIds(String fileName) throws NumberFormatException, IOException{
	    
		List<Long> rVal = new ArrayList<Long>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        
        while ((line = br.readLine()) != null) {
        	rVal.add(Long.valueOf(line));
        }
        br.close();
        
        return rVal;
	}
	
	private static Random rnd = new Random();
	
	public static long[] getRandomN(List<Long> users, int N){
		long[] rVal = new long[N];
		
		for(int i=0;i<N;i++){
			int index = rnd.nextInt(users.size());
			rVal[i] = users.get(index);
			users.remove(index);
		}
		
		return rVal;
	}
	
	public static void startStreaming(String selectedUsersPath, String apiUsersPath, String tempOutputPath, String finalOutputPath) throws ParserConfigurationException, IOException, SAXException {	    	
		
		 List<Long> userIds = getUserIds(selectedUsersPath);
		 
		 List<APIUser> apiUsers = APIUser.getUsers(apiUsersPath, -1);

		 ExecutorService executor = Executors.newCachedThreadPool();
		 
		 for(int i=0;i<apiUsers.size();i++) {
			 APIUser user = apiUsers.get(i);
			 
			 long[] currentUsers = getRandomN(userIds, 5000);
			 StreamWorkerClient client = new StreamWorkerClient(user, currentUsers, tempOutputPath, i);
			   
             executor.execute(client);		     
		 }
		 
		 CronJob job = new CronJob(tempOutputPath, finalOutputPath);
		 executor.execute(job);		 
	 }    	 
	 
}
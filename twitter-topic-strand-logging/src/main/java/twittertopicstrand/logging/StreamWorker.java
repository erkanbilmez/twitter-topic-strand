package twittertopicstrand.logging;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.ArrayUtils;
import org.xml.sax.SAXException;

import twitter4j.UserFactory;

public class StreamWorker {	
	
	public static long[] getUserIds(String fileName) throws NumberFormatException, IOException{
	    
		long[] rVal;
		List<Long> temp = new ArrayList<Long>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        
        while ((line = br.readLine()) != null) {
        	temp.add(Long.valueOf(line));
        }
        br.close();
        
        Long[] l = (Long[]) temp.toArray(new Long[temp.size()]);
        rVal = ArrayUtils.toPrimitive(l);
        
        return rVal;
	}
	
	 public static void startStreaming(String selectedUsersPath, String apiUsersPath, String tempOutputPath, String finalOutputPath) throws ParserConfigurationException, IOException, SAXException {	    	
		
		 long[] userIds = getUserIds(selectedUsersPath);
		 
		 List<APIUser> apiUsers = APIUser.getUsers(apiUsersPath, -1);

		 ExecutorService executor = Executors.newCachedThreadPool();
		 
		 for(int i=0;i<apiUsers.size();i++) {
			 
			 APIUser user = apiUsers.get(i);
			 
			 long[] currentUsers = Arrays.copyOfRange(userIds, i * 5000, (i+1) * 5000);
			 
			 StreamWorkerClient client = new StreamWorkerClient(user, currentUsers, tempOutputPath, i);
			   
             executor.execute(client);		     
		 }
		 
		 CronJob job = new CronJob(tempOutputPath, finalOutputPath);
		 executor.execute(job);		 
	 }    	 
	 
}
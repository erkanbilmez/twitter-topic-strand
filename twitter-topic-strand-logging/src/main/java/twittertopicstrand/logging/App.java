package twittertopicstrand.logging;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.internal.json.StatusJSONImpl;
import twitter4j.internal.json.UserJSONImpl;
import twitter4j.internal.org.json.JSONException;
import twittertopicstrand.util.FileOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws ParserConfigurationException, IOException, SAXException, TwitterException, JSONException  {
	
    	Twitter twitter = APIUser.getTwitters("/home/mll2/Desktop/twitter-data/api-users.xml", 1).get(0);
    	Status myUser = twitter.showStatus(358722235705483264L);

//    	String s = myUser.toFriendlyString();
//    	FileOperations.writeFile(s, "/home/mll2/Desktop/x.txt");
    	
    	String s = FileOperations.readAllText("/home/mll2/Desktop/x.txt");
    	
    	//User u = UserJSONImpl.fromString(s);
    	
		twitter4j.internal.org.json.JSONObject obj = new twitter4j.internal.org.json.JSONObject(s);
		UserJSONImpl u = new UserJSONImpl(obj);
		
		System.out.println(u.getId());
//			
//		
//		StatusJSONImpl s1 = new StatusJSONImpl(obj);			
//			
    	
//    	
//    	System.out.println("hebe");
//    	
//    	System.out.println(u.getId());
//    		
			
// 358722235705483264
		    
	}
}
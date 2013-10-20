package twittertopicstrand.logging;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import twitter4j.TwitterException;

public class App {
	
    public static void main( String[] args ) throws ParserConfigurationException, IOException, SAXException, TwitterException {
    	
//    	String selectedUsersPath = "/home/twtuser/logging/src-data/selectedUserIds.txt";
//    	String apiUsersPath = "/home/twtuser/logging/src-data/api-users.xml";
//    	String tempOutputPath = "/home/twtuser/logging/tmp";
//    	String finalOutputPath = "/home/twtuser/logging/tweets";
//    	
//    	StreamWorker.startStreaming(selectedUsersPath, apiUsersPath, tempOutputPath, finalOutputPath);
    	
    	CronJob cj = new CronJob("/home/twtuser/logging/tmp", "/home/twtuser/logging/tweets2");
    	cj.tick();
    	
    }
}
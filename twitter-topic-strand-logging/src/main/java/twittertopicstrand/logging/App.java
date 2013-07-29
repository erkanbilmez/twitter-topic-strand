package twittertopicstrand.logging;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import twitter4j.LightStatus;
import twitter4j.Status;
import twitter4j.StatusFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserFactory;
import twitter4j.UserMentionEntity;
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

    	String selectedUsersPath = "/home/mll2/Desktop/twitter-data/selectedUserIds.txt";
    	String apiUsersPath = "/home/mll2/Desktop/twitter-data/api-users.xml";
    	String tempOutputPath = "/home/mll2/Desktop/twitter-data/tempTweets";
    	String finalOutputPath = "/home/mll2/Desktop/twitter-data/finalTweets";
    	
    	StreamWorker.startStreaming(selectedUsersPath, apiUsersPath, tempOutputPath, finalOutputPath);
    
    	System.out.println("bye");    	
    }
}
package twittertopicstrand.logging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class APIUser {
    Integer ID;
    String ConsumerKey;
    String ConsumerKeySecret;
    String AccessToken;
    String TokenSecret;

    public APIUser(Integer ID, String consumerKey, String consumerKeySecret, String accessToken, String tokenSecret) {
        this.ID = ID;
        this.ConsumerKey = consumerKey;
        this.ConsumerKeySecret = consumerKeySecret;
        this.AccessToken = accessToken;
        this.TokenSecret = tokenSecret;
    }

    public APIUser(Element element) {
        this.ID = Integer.valueOf(element.getAttribute("id"));
        this.ConsumerKey = getValue("consumer_key", element);
        this.ConsumerKeySecret = getValue("consumer_secret", element);
        this.AccessToken = getValue("access_token", element);
        this.TokenSecret = getValue("token_secret", element);
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }
    
    private ConfigurationBuilder getConfigurationBuilder() {
    	ConfigurationBuilder rVal = new ConfigurationBuilder();
        
    	rVal.setDebugEnabled(true)
                .setOAuthConsumerKey(this.ConsumerKey)
                .setOAuthConsumerSecret(this.ConsumerKeySecret)
                .setOAuthAccessToken(this.AccessToken)
                .setOAuthAccessTokenSecret(this.TokenSecret);
        
        return rVal;
    }

    public static List<APIUser> getUsers(String path, int maxCount) throws ParserConfigurationException, IOException, SAXException {
        List<APIUser> rVal = new ArrayList<APIUser>();

        File users = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(users);
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("user");

        for (int i = 0; i < nodes.getLength(); i++) {
        	if( maxCount != -1 && i >= maxCount ) {
        		break;
        	}
        	
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                rVal.add(new APIUser(element));
            }
        }

        return rVal;
    }
    
    public static List<Twitter> getTwitters(String path, int maxCount) throws ParserConfigurationException, IOException, SAXException {
       
    	List<Twitter> rVal = new ArrayList<Twitter>();
    	List<APIUser> users = getUsers(path, maxCount);
    	
    	for(APIUser user: users){
    		rVal.add(user.getTwitter());
    	}
    	
    	return rVal;
    }

    public Twitter getTwitter(){
        Twitter rVal = null;        

        ConfigurationBuilder cb = this.getConfigurationBuilder();
        TwitterFactory tf = new TwitterFactory(cb.build());

        rVal = tf.getInstance();

        try {
            User user = rVal.verifyCredentials();
        }
        catch (TwitterException te) {

        }
        return rVal;
    }
    
    public TwitterStream GetTwitterStream(){
        TwitterStream rVal = null;

        ConfigurationBuilder cb = getConfigurationBuilder();
        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
        
        rVal = tf.getInstance();

        return rVal;
    }
}
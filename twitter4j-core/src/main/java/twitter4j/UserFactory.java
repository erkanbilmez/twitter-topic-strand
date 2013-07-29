package twitter4j;

import twitter4j.internal.json.StatusJSONImpl;
import twitter4j.internal.json.UserJSONImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserFactory {
	public static User fromString(String s){
		
		GsonBuilder builder = new GsonBuilder();    	
    	Gson gson = builder.create();
    	    	
    	User rVal = (User) gson.fromJson(s, UserJSONImpl.class);
    	
    	return rVal;		
	}
}

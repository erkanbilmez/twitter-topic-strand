package twitter4j;

import twitter4j.internal.json.StatusJSONImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StatusFactory {
	
	public static Status fromString(String s){

    	GsonBuilder builder = new GsonBuilder();    	
    	Gson gson = builder.create();
    	
    	Status rVal = (Status) gson.fromJson(s, StatusJSONImpl.class);
    	
    	return rVal;
    }

}

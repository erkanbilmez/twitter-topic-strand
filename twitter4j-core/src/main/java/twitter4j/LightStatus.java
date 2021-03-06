package twitter4j;

import java.util.Date;

import twitter4j.internal.json.StatusJSONImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LightStatus implements Comparable<LightStatus> {
	
	public long id;
	public long userId;
	public Date createdAt;
	public long retweetedStatusId;
	public long retweetedStatusUserId;
	public String[] hashTags;
	public String text;
	
	public String toJSONString(){
		
		GsonBuilder builder = new GsonBuilder();
    	Gson gson = builder.create();
    	
    	String temp = gson.toJson(this);
    	
    	return temp;		
	}
	
	public static LightStatus fromJSONString(String json){
		
	  	GsonBuilder builder = new GsonBuilder();    	
    	Gson gson = builder.create();
    	
    	LightStatus rVal = gson.fromJson(json, LightStatus.class);
    	
    	for(int i=0;i<rVal.hashTags.length;i++){
    		rVal.hashTags[i] = rVal.hashTags[i].toLowerCase();
    	}
    	
    	return rVal;
	}

	@Override
	public int compareTo(LightStatus arg0) {
		return this.createdAt.compareTo(arg0.createdAt);
	}
	
    @Override
    public boolean equals(Object other){
    	if(other == null)
    		return false;
    	
    	LightStatus o = (LightStatus) other;
    	
    	return (this.id == o.id);
    }
}

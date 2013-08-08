package twittertopicstrand.converter;

import java.util.List;

import twitter4j.LightStatus;
import twitter4j.Status;

public class Tweet
{
	public String id;
	public String retweetID;
	public String userID;
	
	public String createdAt;
	public String dateStr;
	public String timeStr;
	
	public String retweetedBy;
	public int hourID;
	
	public String text;
	
	public List<String> hashTags;
	public List<String> userMentions;
	
	public String ToStatusString()
	{
		String s = "";
		return s;
	}
	
	public String ToLightString(DateConverter dc)
	{
		LightStatus status = new LightStatus();
		
		dc.set(this.createdAt);
		status.createdAt = dc.getDate();
		status.hashTags = (String[])this.hashTags.toArray();
		status.id = Long.valueOf(this.id);
		status.retweetedStatusId = Long.valueOf(this.retweetID);
		status.retweetedStatusUserId = Long.valueOf(this.retweetedBy);
		status.userId = Long.valueOf(this.userID);
		return status.toJSONString();
	}
	
	@Override
	public String toString()
	{
		String hashs = "";
		if (hashTags.size() > 0)
		{
			for (int i=0; i<hashTags.size(); i++)
				hashs += hashTags.get(i) +",";
			hashs = hashs.substring(0, hashs.length()-1);
		}
		
		String mentions = "";
		if (userMentions.size() > 0)
		{
			for (int i=0; i<userMentions.size(); i++)
				mentions += userMentions.get(i) +",";
			mentions = mentions.substring(0, mentions.length()-1);
		}
		
		return timeStr + 
			"|" + id + 
			"|" + userID +
			"|" + retweetID +
			"|" + retweetedBy +
			"|" + hourID + 
			"|" + mentions +
			"|" + hashs +
			"|" + text;
	}
}
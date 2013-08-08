package twittertopicstrand.converter;

import java.util.ArrayList;


public class TweetParser{
	public Tweet tweet;
	public boolean hasError;
	public String errorMessage;
	
	public TweetParser () {}
	
	public void ParseData (String line, String date)
	{
		try 
		{
			Tweet tw = new Tweet();
			
			int [] indexes = new int[10];
			indexes[0] = 0;
			indexes[9] = line.length();
			for (int i=0; i<indexes.length-2; i++)
				indexes[i+1] = line.indexOf("|", indexes[i]+1);
			
			String [] datalist = new String[9];
			for (int i=0; i<datalist.length;i++)
				if (i == 0)
					datalist[i] = line.substring(indexes[i], indexes[i+1]);
				else
					datalist[i] = line.substring(indexes[i]+1, indexes[i+1]);
			
			tw.timeStr = datalist[0];
			tw.dateStr = date;
			
			DateConverter dc = new DateConverter ();
			if (!dc.set(tw.timeStr, tw.dateStr))
			{
				this.hasError = true;
				this.errorMessage = "Date Parsing Exception: Could not parse '" + tw.createdAt + "'";
				return;
			}
			else
			{
				tw.createdAt = dc.getLongStr();
			}
			
			tw.id = datalist[1];
			tw.userID = datalist[2];
			tw.retweetID = datalist[3];
			tw.retweetedBy = datalist[4];
			tw.hourID = Integer.valueOf(datalist[5]);

			String mentions = datalist[6];
			tw.userMentions = new ArrayList<String>();
			if (!mentions.equals(""))
			{
				String [] ment = mentions.split(",");
				for (int i=0; i<ment.length; i++)
					tw.userMentions.add(ment[i]);
			}
			
			String hashs = datalist[7];
			tw.hashTags = new ArrayList<String>();
			if (!hashs.equals(""))
			{
				String [] hash = hashs.split(",");
				for (int i=0; i<hash.length; i++)
					tw.hashTags.add(hash[i]);
			}
			
			tw.text = datalist[8];
			this.hasError = false;
			this.tweet = tw;
		} 
		catch (Exception ex)
		{
			this.hasError = true;
			this.errorMessage = ex.getMessage();
		}
	}
	
	public void ParseJSON (String json, DateConverter dc)
	{
		errorMessage = "";
		try {
			Tweet tw = new Tweet();
			
			tw.createdAt = "";
			tw.timeStr = "";
			tw.dateStr = "";
			
			if (json.contains("{createdAt="))
				tw.createdAt = GetData(json,0,"{createdAt=", ",","default", "error");
			
			
			if (!dc.set(tw.createdAt))
			{
				this.hasError = true;
				this.errorMessage = "Date Parsing Exception: Could not parse '" + tw.createdAt + "'";
				return;
			}
			else
			{
				String [] result = dc.getSplitStr();
				tw.timeStr = result[0];
				tw.dateStr = result[1];
			}
			
			tw.id = "";
			if (json.contains(", id="))
				tw.id = GetData(json,0,", id=", ",","default", "error");
			
			tw.userID = "";
			if (json.contains(", user=UserJSONImpl{id="))
				tw.userID = GetData2(json,0,", user=UserJSONImpl{id=", ",","default", "error");
			
			tw.text = "";
			if (json.contains(", text='"))
				tw.text = GetData(json,0,", text='", "', source=","default", "error");
			
			tw.hourID = DateConverter.ToInt(dc.getDate());
				
			tw.retweetID = "";
			if (json.contains(", retweetedStatus="))
				tw.retweetID = GetData(json,0,", retweetedStatus=", ", userMentionEntities=","default", "error");
			
			if (tw.retweetID.equals("null"))
			{
				tw.retweetID =  "";
				tw.retweetedBy = "";
			}
			else
			{
				tw.retweetID = GetData(tw.retweetID, 0, ", id=", ",", "default", "error");
				tw.retweetedBy = GetData(json,0,", user=UserJSONImpl{id=", ",","default", "error");				
			}
			
			tw.hashTags = new ArrayList<String>();
			if (json.contains(", hashtagEntities=["))
			{
				String tags = GetData(json,0,", hashtagEntities=[", "]","default", "error");
				if (!tags.equals(""))
				{
					String key = ", text='";
					int keylen = key.length();
					int index = tags.indexOf(key);
					while (index != -1)
					{
						tw.hashTags.add(GetData(tags,0,key, "'","default", "error"));
						tags = tags.substring(index + keylen);
						index = tags.indexOf(key);
					}
				}	
			}
			
			tw.userMentions = new ArrayList<String>();
			if (json.contains(", userMentionEntities=["))
			{
				String tags = GetData(json,0,", userMentionEntities=[", "]","default", "error");
				if (!tags.equals(""))
				{
					String key = ", id=";
					int keylen = key.length();
					int index = tags.indexOf(key);
					while (index != -1)
					{
						tw.userMentions.add(GetData(tags,0,key, "}","default", "error"));
						tags = tags.substring(index + keylen);
						index = tags.indexOf(key);
					}
				}
			}
			this.tweet = tw;
		} catch (Exception e) {
			this.hasError = true;
			this.errorMessage = e.getMessage();
		}
	}
	
	private static String GetData (String text, int startPos, String startText, String endText, String defaultText, String error)
	{
		try {
			String t = text.substring(startPos);
			
			int start = t.indexOf(startText) + startText.length();
			int end = t.indexOf(endText, start);
			
			if (start == end)
				return "";
			
			return t.substring(start, end);
		}
		catch (Exception ex)
		{
			return error;
		}
	}
	
	private static String GetData2 (String text, int startPos, String startText, String endText, String defaultText, String error)
	{
		try {
			String t = text.substring(startPos);
			
			int start = t.lastIndexOf(startText) + startText.length();
			int end = t.indexOf(endText, start);
			
			if (start == end)
				return "";
			
			return t.substring(start, end);
		}
		catch (Exception ex)
		{
			return error;
		}
	}
}

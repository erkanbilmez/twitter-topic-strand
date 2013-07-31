package twittertopicstrand.analyzing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import twitter4j.LightStatus;

public class UserAnalyzer {
	
	public Map<Long, UserTimeline> Users;
	public int FirstHour;
	public int LastHour;
	public int TweetCount;
	public String Hashtag = "";
	public Map<Integer, Integer> HourCounter;
	
	public List<UserTimeline> Heroes;
	public List<UserTimeline> Veterans;
	
	public UserAnalyzer (String hashtag, LightStatus[] statuses ) {
		this.Hashtag = hashtag;
		this.Users = new HashMap<Long, UserTimeline>();
		this.FirstHour = 0;
		this.LastHour = 0;
		this.TweetCount = 0;
		this.HourCounter = new HashMap<Integer, Integer>();
	}	

	public void Load (List<LightStatus> mlist)	{		
		for (LightStatus ms : mlist)
			Add (ms.userId, ms.createdAt);
	}
	
	/* Find Veterans */
	public void AnalyzeVeterans ()	{
		//Parameters
		int MinTopicLength = 3 * 24 ; // 3 days
		double DiscussionLengthRatio = 0.5; //user'ýn topic'in ne kadarýnda 
		int MinAvgTweetPerHour = 1;
		//End of Parameters
		
		
		for (UserTimeline utl : this.Users.values())
		{
			///do to: Veteran parametrelerine göre düzelt
			
			if (this.Duration() < MinTopicLength)
				continue;
			
			if (utl.Duration() < this.Duration() * DiscussionLengthRatio)
				continue;
			
			if (utl.GetAverageTweetPerHour() < MinAvgTweetPerHour)
				continue;
			
			this.Veterans.add(utl);
		}
	}
	
	/* Find Heroes */
	public void AnalyzeHeroes()	{
		//Parameters
		int MinPeakTweetCount = 100;
		int TweetRatio = 10;
		//End of Parameters
		
		double AverageTweetPerUser = (double) this.TweetCount / (double) GetParticipantCount();
		
		for (UserTimeline utl : this.Users.values())
		{
			///do to: Hero parametrelerine göre düzelt
			
			if (utl.TweetCount * TweetRatio < AverageTweetPerUser )
				continue;
			
			if (utl.GetPeakCount() < MinPeakTweetCount)
				continue;
			
			this.Heroes.add(utl);
		}
	}
	
	public int GetTweetCount (int hourId)	{
		Integer value = this.HourCounter.get(hourId);
		return value == null ? 0 : value;
	}
	
	/* Total Participant count  */
	public int GetParticipantCount()	{
		return this.Users.size();
	}
	
	/* Active Participant count in a specific hour */
	public int GetParticipantCount (int hourId)	{
		int count = 0;
		for (UserTimeline utl : this.Users.values())
			if (utl.FirstHour <= hourId && hourId <= utl.LastHour)
				count ++;
		return count;
	}
	
	/* Get Participant Id list */
	public long[] GetParticipants ()	{
		long [] h = new long[this.Users.size()];
		for (int i=0; i<h.length; i++)
			h[i] =  this.Users.get(i).UserId;
		return h;
	}
	
	/* Get Participant Id list for specific hour */
	public List<Long> GetParticipants (int hourId)	{
		List<Long> h = new ArrayList<Long>();
		for (UserTimeline utl : this.Users.values())
			if (utl.FirstHour <= hourId && hourId <= utl.LastHour)
				h.add(utl.UserId);
		return h;
	}
	
	/* Get Veteran Id list */
	public long[] GetVeterans ()	{
		long [] v = new long[this.Veterans.size()];
		for (int i=0; i<v.length; i++)
			v[i] =  this.Veterans.get(i).UserId;
		return v;
	}
	
	/* Get Veteran Id list for specific hour */
	public List<Long> GetVeterans (int hourId)	{
		List<Long> h = new ArrayList<Long>();
		for (UserTimeline utl : this.Veterans)
			if (utl.FirstHour <= hourId && hourId <= utl.LastHour)
				h.add(utl.UserId);
		return h;
	}
	
	/* Total Veteran count */
	public int GetVeteranCount ()	{
		return this.Veterans.size();
	}
	
	/* Active Veteran count in a specific hour */
	public int GetVeteranCount (int hourId)	{
		int count = 0;
		for (UserTimeline utl : this.Veterans)
			if (utl.FirstHour <= hourId && hourId <= utl.LastHour)
				count ++;
		return count;
	}	
	
	/* Get Hero Id list */
	public long[] GetHeroes ()	{
		long [] h = new long[this.Heroes.size()];
		for (int i=0; i<h.length; i++)
			h[i] =  this.Heroes.get(i).UserId;
		return h;
	}
	
	/* Get Hero Id list for specific hour */
	public List<Long> GetHeroes (int hourId)	{
		List<Long> h = new ArrayList<Long>();
		for (UserTimeline utl : this.Heroes)
			if (utl.FirstHour <= hourId && hourId <= utl.LastHour)
				h.add(utl.UserId);
		return h;
	}

	/* Total Hero count */
	public int GetHeroCount ()	{
		return this.Heroes.size();
	}
	
	/* Active Hero count in a specific hour */
	public int GetHeroCount (int hourId)	{
		int count = 0;
		for (UserTimeline utl : this.Heroes)
			if (utl.FirstHour <= hourId && hourId <= utl.LastHour)
				count ++;
		return count;
	}	
	
	private void Add (long userId, Date createdAt)	{
		int hourId = GetHourId(createdAt);
		
		if (Users.containsKey(userId))
			Users.get(userId).Add(hourId);
		else
			Users.put(userId, new UserTimeline(userId, hourId));
		
		Integer value = this.HourCounter.get(hourId);
		if (value == null)
			this.HourCounter.put(hourId, 1);
		else
			this.HourCounter.put(hourId, value+1);
		
		if (FirstHour == 0 || FirstHour > hourId)
			FirstHour = hourId;
		if (LastHour < hourId)
			LastHour = hourId;
		
		this.TweetCount++;
	}

	public int Duration ()	{
		return this.LastHour - this.FirstHour + 1;
	}

	public static long GetHourId(DateTime date)	{
    	long secsSince1970 = date.getMillis();

    	secsSince1970 /= (1000 * 60 * 60); // from 1970    	
    	
    	long secsBetween1970and2012 = new DateTime(2012, 1, 1, 0, 0).getMillis();
    	
    	secsBetween1970and2012 /= (1000 * 60 * 60) ; // from 2012 - 1970
    	
    	long secsSince2012 = secsSince1970 - secsBetween1970and2012;
		
		return secsSince2012;
	}	
	
	public static int GetHourId(Date date)	{
		return 0;
	}	
	
	public class UserTimeline	{
		public Map<Integer, Integer> HourCounter;
		public int FirstHour;
		public int LastHour;
		public int TweetCount;
		public long UserId;
		
		public UserTimeline (long userId, int hourId)		{
			this.UserId = userId;
			this.FirstHour = hourId;
			this.LastHour = hourId;
			this.TweetCount = 1;
			this.HourCounter = new HashMap<Integer, Integer>();
			
			Add(hourId);
		}
		
		public void Add (int hourId)		{
			this.TweetCount++;
			
			Integer value = this.HourCounter.get(hourId);
			if (value == null)
				this.HourCounter.put(hourId, 1);
			else
				this.HourCounter.put(hourId, value+1);
			
			if (FirstHour > hourId)
				FirstHour = hourId;
			if (LastHour < hourId)
				LastHour = hourId;
		}
		
		/* Number of tweet in peak hour */
		public int GetPeakCount ()		{
			return Collections.max(this.HourCounter.values());
		}
		
		public int Duration ()		{
			return this.LastHour - this.FirstHour + 1;
		}
		
		public double GetAverageTweetPerHour ()		{
			return ((double)this.TweetCount / (double)Duration());
		}
	}
}

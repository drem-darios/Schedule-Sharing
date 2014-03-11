package Client;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable{

	public String user;
	public String date;
	public String startTime;
	public String endTime;
	public String comments;
	public List guestList;

	//empty Constructor
	public Event()
	{
		
	}
	//an event takes in the user, the date, start/end time of an event and comments from the user
	public Event(String user, String date, String startTime, String endTime, String comments, List guestList)
	{
		this.user = user;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.comments = comments;
		this.guestList = guestList;
	}
	
	//getters and setters
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
//check to see if the time is am or pm
	public boolean isPM(String time)
	{
		if(time.contains("PM"))
			return true;
		else
			return false;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public List getGuestList() {
		return guestList;
	}
	
	public void setGuestList(List guestList) {
		this.guestList = guestList;
	}
	
}

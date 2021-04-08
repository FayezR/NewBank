package newbank.server;

import java.time.LocalTime;

public class UserID {
	private String key;
	private LocalTime timeAtLastActivity;
	
	public UserID(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "CustomerID [key=" + key + ", getKey()=" + getKey() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

	//setter for last activity
	public void setTimeAtLastActivity(LocalTime lastActivityTime) {
		timeAtLastActivity = lastActivityTime;
	}
	
	//getter for last activity
	public LocalTime getTimeAtLastActivity() {
		return timeAtLastActivity;
	}
	
	
}

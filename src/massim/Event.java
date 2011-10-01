package massim;

import java.util.ArrayList;
import java.util.List;


public class Event implements Comparable<Event> {
	public int timeStep;
	public CMD cmd;
	public List<String> args;
	
	public enum CMD {CHCOLOR,MOVEGOAL,INVALID} // set of possible commands used in the script
	
	public Event() {
		
	}
	
	public Event(int t, String c,List<String> a) {
		timeStep = t;		
		args = new ArrayList<String>(a);		
		
		if (c.equals("chcolor"))
			cmd = CMD.CHCOLOR;
		else if (c.equals("moveg"))
			cmd = CMD.MOVEGOAL;
		else
			cmd = CMD.INVALID;
		
	}
	
	public Event(int t, CMD c,List<String> a) {
		timeStep = t;
		cmd = c;
		args = new ArrayList<String>(a);
	}
	
	public int compareTo(Event e) {
		//Event e = (Event)o;
		
		int t1 = timeStep;
		int t2 = e.timeStep;
		
		if (t1 > t2) 
			return 1;
		if (t1 < t2) 
			return -1;
		else 
			return 0;
				
	}
}

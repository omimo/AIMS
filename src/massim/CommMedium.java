package massim;

import java.util.HashMap;

/**
 * CommMedium.java
 * Responsible for all the communications within a team of 
 * agents
 *
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 */
public class CommMedium {

	private int numOfAgent;
	Agent[] agents;
	String[][] channels;
	
	/**
	 * The default constructor
	 * @param n number of agents 
	 */
	public CommMedium(int n) {
		numOfAgent = n;
					
		// Initializing all the channels
		channels = new String[n][n];
		for (int i=0;i<n;i++)
			for (int j=0;j<n;j++)
				channels[i][j]="";
	}
	
	/**
	 * Puts the msg into the receiver's special buffer for the sender
	 * @param sender The sender agent's id
	 * @param receiver The receiver agent's id
	 * @param msg The message
	 */
	public void send(int sender, int receiver, String msg) {	
		// Might add the sender,receiver info to the head of msg
		// which will be used while decoding by a Message class
		channels[receiver][sender] = msg;
	}
	
	/**
	 * Puts the msg into all the agent's special buffer for the sender
	 * @param sender The sender agent's id
	 * @param msg The message
	 */
	public void broadcast(int sender, String msg) {
		// Might add the sender,receiver info to the head of msg
		// which will be used while decoding by a Message class
		
		for (int i=0;i<numOfAgent;i++)		
			if (i!=sender)
				channels[i][sender] = msg;		
	}

	
	public String receive(int receiver) {
		
		String out="";
		
		for(int i=0;i<channels[receiver].length;i++)
		{			
			if (!channels[receiver][i].isEmpty())
			{
				out = channels[receiver][i];
				channels[receiver][i] = "";
				return out;
			}
		}
		return out;
	}
	
	/**
	 * To check whether the communication medium is empty. Means there 
	 * were no communication during the last iteration
	 * @return true if all the channels for all the agents are empty. 
	 * 		   false otherwise
	 */
	public boolean isEmpty() {
		
		for (int i=0;i<numOfAgent;i++)
			for (int j=0;j<numOfAgent;j++)
				if (channels[i][j] != "")
					return false;
		return true;
	}
	
	@Override
	public String toString() {
		String s = "";
		for (int i=0;i<channels.length;i++)
		{
			s += "Agent "+ i+ "'s channels: \n";
			String[] b = channels[i];
			for (int j=0;j<b.length;j++)
			{
				if (i!=j)
				{
					s += "Agent "+ j+ " : ";
					s += b[j];
					s += "\n";
				}
			}
		}
		
		
		return s;
	}
	
}

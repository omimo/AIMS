package massim;

import java.util.HashMap;

/**
 * CommMedium.java
 * Responsible for all the communications within a team of 
 * agents
 *
 * @author Omid Alemi
 * @version 1.1 2011/10/07
 */
public class CommMedium {

	
	String[][] channels;
	int numOfChannels;
	/**
	 * The default constructor
	 * 
	 */
	public CommMedium() {

		numOfChannels = Team.teamSize;
		// Initializing all the channels
		channels = new String[numOfChannels][numOfChannels];
		for (int i=0;i<numOfChannels;i++)
			for (int j=0;j<numOfChannels;j++)
				channels[i][j]="";
	}
	
	/**
	 * Puts the msg into the receiver's special channel for the sender
	 * 
	 * @param sender 				The sender agent's id
	 * @param receiver 				The receiver agent's id
	 * @param msg 					The message
	 */
	public void send(int sender, int receiver, String msg) {	

		if (receiver != sender)
			channels[sender][receiver] = msg;
	}
	
	/**
	 * Puts the msg into all the agent's special channels for the sender
	 * 
	 * @param sender 				The sender agent's id
	 * @param msg 					The message
	 */
	public void broadcast(int sender, String msg) {
		
		for (int i=0;i<Team.teamSize;i++)		
			if (i!=sender)
				channels[sender][i] = msg;		
	}

	
	/**
	 * Returns the next available message in the receiver's incoming communication channels.
	 * Returns an empty message if there is no message left on the channels
	 * 
	 * @param receiver				The id of the receiver agent
	 * @return						The message 
	 */
	public String receive(int receiver) {
		
		String out="";
		
		for(int i=0;i<channels.length;i++)
		{			
			if (!channels[i][receiver].isEmpty())
			{
				out = channels[i][receiver];
				channels[i][receiver] = "";
				return out;
			}
		}
		return out;
	}
	
	/**
	 * To check whether the communication medium is empty. Means there 
	 * were no communication during the last iteration
	 * 
	 * @return 					true if all the channels for all the agents are empty. 
	 * 		   					false otherwise
	 */
	public boolean isEmpty() {
		
		for (int i=0;i<Team.teamSize;i++)
			for (int j=0;j<Team.teamSize;j++)
				if (channels[i][j] != "")
					return false;
		return true;
	}
	
	/**
	 * Clears all the channels
	 */
	public void clear() {
		for (int i=0;i<numOfChannels;i++)
			for (int j=0;j<numOfChannels;j++)
				channels[i][j]="";
	}
	
	/**
	 * Used for the debugging purposes
	 * Generates a string representation of all the communication channels
	 * and their values
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i=0;i<channels[0].length;i++)
		{
			s += "[Agent "+ i+ "'s incoming channels: ]\n";
						
			for (int j=0;j<channels.length;j++)
			{
				if (i!=j)
				{
					s += "Agent "+ j+ " : ";
					s += channels[j][i];
					s += "\n";
				}
			}
		}				
		return s;
	}
	
}

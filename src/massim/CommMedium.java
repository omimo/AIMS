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
	String[][] buffers;
	
	/**
	 * The default constructor
	 * @param n number of agents 
	 */
	public CommMedium(int n) {
		numOfAgent = n;
					
		// Initializing all the buffers
		buffers = new String[n][n-1];
		for (int i=0;i<n;i++)
			for (int j=0;j<n-1;j++)
				buffers[i][j]="";
	}
	
	/**
	 * Puts the msg into the receiver's special buffer for the sender
	 * @param sender The sender agent's id
	 * @param receiver The receiver agent's id
	 * @param msg The message
	 */
	public void send(int sender, int receiver, String msg) {
		
	}
	
	/**
	 * Puts the msg into all the agent's special buffer for the sender
	 * @param sender The sender agent's id
	 * @param msg The message
	 */
	public void broadcast(int sender, String msg) {
		
	}

	/**
	 * Returns the tuples of the <sender,msg> for all the incoming 
	 * messages for the receiver agent  
	 * @param receiver The receiver agent
	 * @return Tuples of the <sender,msg>
	 */
	public String[] receive(int receiver) {
		 
		return null;
	}
	
	/**
	 * To check whether the communication medium is empty. Means there 
	 * were no communication during the last iteration
	 * @return true if all the buffers for all the agents are empty. 
	 * 		   false otherwise
	 */
	public boolean isEmpty() {
		 
		return false;
	}
	
}

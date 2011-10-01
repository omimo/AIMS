package massim;

import java.util.HashMap;

public class CommMedium {

	private int numOfAgent;
	Agent[] agents;
	HashMap<Agent,String[]> buffers;
	
	public CommMedium(Team team) {
		
	}
	
	/**
	 * Puts the msg into the receiver's special buffer for the sender
	 * @param sender The sender agent
	 * @param receiver The receiver agent
	 * @param msg The message
	 */
	public void send(Agent sender, Agent receiver, String msg) {
		
	}
	
	/**
	 * Puts the msg into all the agent's special buffer for the sender
	 * @param sender The sender agent
	 * @param msg The message
	 */
	public void broadcast(Agent sender, String msg) {
		
	}

	/**
	 * Returns the tuples of the <sender,msg> for all the incoming messages for the receiver agent  
	 * @param receiver The receiver agent
	 * @return Tuples of the <sender,msg>
	 */
	public HashMap<Agent,String> receive(Agent receiver) {
		 
		return null;
	}
	
	/**
	 * To check whether the communication medium is empty. Means there were no communication during the last iteration
	 * @return true if all the buffers for all the agents are empty. false otherwise
	 */
	public boolean isEmpty() {
		 
		return false;
	}
	
}

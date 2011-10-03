package massim;

import java.util.HashMap;

/**
 * Team.java
 * 
 *
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 */
public class Team {

	private Agent[] agents;
	private CommMedium communicationMeduim;
	
	/**
	 * Default constructor
	 */
	public Team() {
		
	}
	
	/** 
	 * Constructor, getting a set of agents as the members of the team
	 * @param agents An array of agents
	 */
	public Team(Agent[] agents) {
		init(agents);		
	}
	
	/**
	 * Initializes the team object
	 * @param agents Array of initial agents 
	 */
	public void init(Agent[] agents) {
		// communicationMeduim = new CommMedium();
		// this.agents = agents
		
		// for each agent a
		//     a.init(thisjjjj)
		
		// Set all the results to the initial values
	}
	
	/**
	 * Called by the simulator in each step of simulation
	 * @return ENDSIM code if the simulation is over
	 */
	public int step(Board board) {
		
		// 0. Update Agents Percepts
		// agPos[] = position of all the agents in the team
		// for each agent a in agents[]	
		//     a.perceive(board, agPos[])
		
		
		// 1. Communication Phase
		// noMsgPass = 5  
		// do
		//     for each agent a in agents[]
		//         a.doSend();
		//     for each agent a in agents[]
		//         a.doReceive();

		// ?? WEHRE SHOULD WE GET THE STATUS CODE FROM THE AGENT? 
		// (in doReceive or in a separate method just for that?) 
		
		// 	   if (there was no communication)
		//         noMsgPass--;
		//     else 
		//         noMsgPass = 5;
		// while (there was at least one communication && noMsgPass != 0)
		
		
		// 1. Action Phase
		// for each agent a in agents[]
		//     a.act()
		
		return 0;
	}
	
	/**
	 * Returns the team state, for the frontend usages.
	 * team state should consist all the information about the agents' 
	 * position, resources, etc. 
	 * @return
	 */
	public TeamState getTeamState() {
		
		return null;
	}
	
	/**
	 * Calls the send method of the communication medium
	 * @param sender The sender Agent
	 * @param receiver The reciever Agent
	 * @param msg The message
	 */
	public void send(Agent sender, Agent receiver, String msg) {
		communicationMeduim.send(sender, receiver, msg);
		//or if the communicationMedium deals with the integers
		//instead of objects, the sender and receiver objects
		//can be mapped to their integer indices before calling
		//the method in communicationMedium
	}
	
	/**
	 * Calls the receive method of the communication medium.
	 * @param receiver The receiver agent
	 * @return The list of all incoming messages for the receiver agent
	 */
	public HashMap<Agent,String> receive(Agent receiver) {
		//or if the communicationMedium deals with the integers
		//instead of objects, the receiver objects
		//can be mapped to its integer indices before calling
		//the method in communicationMedium
		return communicationMeduim.receive(receiver);		
	}
	
}

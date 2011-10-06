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

//	private Agent[] agents;	
	private CommMedium communicationMeduim;  
	private TeamContext teamState;
	
	public static enum TeamStepCode {OK, DONE, ERR}
	
	/**
	 * Default constructor
	 */
	public Team() {
		communicationMeduim = new CommMedium(Simulator.simParams.numOfAgentsPerTeam);
	}
	
	
	/**
	 * Initializes the team object
	 * @param agents Array of initial agents 
	 */
	public void init(TeamContext ts) {
		// Set all the results to the initial values (if any)
		
		teamState = ts;		
		
				
		for (Agent a : teamState.agents())
			a.init(this);
	}
	
	/**
	 * Called by the simulator in each step of simulation
	 * @return ENDSIM code if the simulation is over
	 */
	public TeamStepCode step(SimState simState) {
		
		// 0. Update Agents Percepts		
		// for each agent a_i in agents[]
		//     a_i.perceive(simSt.board(), simSt.costVerctor(i), simSt.goal(i), ts.agPositions());
		
		
		
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
		
		return TeamStepCode.OK;
	}
	
	/**
	 * Returns the team state, for the frontend usages.
	 * team state should consist all the information about the agents' 
	 * position, resources, etc. 
	 * @return
	 */
	public TeamContext teamState() {		
		
		return teamState;
	}
	
	/**
	 * Calls the send method of the communication medium
	 * @param sender The sender Agent
	 * @param receiver The reciever Agent
	 * @param msg The message
	 */
	public void send(Agent sender, Agent receiver, String msg) {
		//Map from sender object to its integer id
		
		communicationMeduim.send(sender.id(), receiver.id(), msg);
		//or if the communicationMedium deals with the integers
		//instead of objects, the sender and receiver objects
		//can be mapped to their integer indices before calling
		//the method in communicationMedium
	}
	
	/**
	 * Calls the send method of the communication medium
	 * @param sender The sender Agent
	 * @param receiver The reciever Agent
	 * @param msg The message
	 */
	public void send(int sender, int receiver, String msg) {
		
		
		communicationMeduim.send(sender, receiver, msg);
		//or if the communicationMedium deals with the integers
		//instead of objects, the sender and receiver objects
		//can be mapped to their integer indices before calling
		//the method in communicationMedium
	}
	
	
	/**
	 * Calls the send method of the communication medium
	 * @param sender The sender Agent
	 * @param receiver The reciever Agent
	 * @param msg The message
	 */
	public void broadcast(int sender, String msg) {
		
		
		communicationMeduim.broadcast(sender, msg);
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
	public String[] receive(Agent receiver) {
		//or if the communicationMedium deals with the integers
		//instead of objects, the receiver objects
		//can be mapped to its integer indices before calling
		//the method in communicationMedium
		return communicationMeduim.receive(receiver.id());		
	}
	
	/**
	 * Calls the receive method of the communication medium.
	 * @param receiver The receiver agent
	 * @return The list of all incoming messages for the receiver agent
	 */
	public String[] receive(int receiver) {
		//or if the communicationMedium deals with the integers
		//instead of objects, the receiver objects
		//can be mapped to its integer indices before calling
		//the method in communicationMedium
		return communicationMeduim.receive(receiver);		
	}
}

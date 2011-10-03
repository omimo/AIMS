package massim;

/**
 * Agent.java
 * An abstract class for all the agents to be used in the 
 * simulator
 *
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 */
public abstract class Agent {

	private RowCol pos;
	private Team team;
	
	private RowCol[] myPath;  // Or possibly of type Path
	
	/**
	 * Default constructor
	 */
	public Agent() {
		
	}
	
	/** 
	 * Initializes the agent
	 */
	public void init(Team t) {
		// set the attributes to their initial values
		setTeam(t);
		
	}
	
			
	/**
	 * Where agent performs its action
	 * @return 0 if it was successful, -1 for error (might not 
	 *           be the right place for this)
	 */
	public int act() {
		// just move to the next position in the path as the 
		// default action, maybe do nothing as default
		return 0;
	}
	
		
	/**
	 * Called by the Team in order to enable the agent to update 
	 * its information about the environment
	 * @param board The current state of the board
	 * @param agentsPos The current position of the agent's teammates 
	 *        on the board
	 */
	public void perceive(Board board, RowCol[] agentsPos) {
		// Keep the necessary information private 
	}
	
	/**
	 * Sends all the outgoing messages, if any, in the current iteration 
	 * in the team step()
	 */
	public void doSend() {
		// nothing as default
	}
	
	/**
	 * Receives all the incoming message, if any, from other agents in 
	 * the current iteration in the team cycle 
	 */
	public void doReceive() {
		// nothing as default
	}
	
	/**
	 * Sets the team that the agent belongs to
	 * @param t The reference to the team
	 */
	public void setTeam(Team t) {
		team = t;
	}
}

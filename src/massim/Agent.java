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

	private int id;
	private RowCol pos;
	private Team team;
		
	
	private Path path;  // Or possibly of type Path
	private int points = 0;
	private int resources = 0;
	
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
	 * 
     * @param board The current state of the board
     * @param costVerctor The cost verctor of this agent
     * @param goals The goal for this agent
     * @param agentsPos the current position of all the agents within
     *        the team
     */
	public void perceive(Board board, int []costVerctor, Goal goals, RowCol[] agentsPos) {
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
	
	/**
	 * 
	 * @return The id attribute of the class
	 */
	public int id() {
		return id;
	}
	
	/**
	 * 
	 * @return The amount of points the agent has earned
	 */
	public int points() {
		return points;
	}
	
	/**
	 * 
	 * @return The amount of resources that the agent owns
	 */
	public int resources() {
		return resources;
	}
	
	/**
	 * Increases the points by the specified amount
	 * @param amount
	 */
	public void incPoints(int amount) {
		points += amount;
	}
	
	/**
	 * Decreases the points by the specified amount
	 * @param amount
	 */
	public void decPoints(int amount) {
		points -= amount;
	}
	
	/**
	 * Increases the resources by the specified amount
	 * @param amount
	 */
	public void incResources(int amount) {
		resources += amount;
	}
	
	/**
	 * Decreases the resources by the specified amount
	 * @param amount
	 */
	public void decResources(int amount) {
		resources -= amount;
	}
	
}

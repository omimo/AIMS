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
	private Team team;
		
		
	// *** The beliefs
	// Personal beliefs (mental notes)
	private int points = 0;
	private int resources = 0;	
	// Percepts
	private int[] costVector;
	private Path path;  // Or possibly of type Path
	private RowCol pos;
	private Goal myGoal;
	private RowCol[] agPos;
	// ****
	
	
	/**
	 * Default constructor
	 */
	public Agent(int id) {
		this.id = id;
	}
	
	/** 
	 * Initializes the agent
	 */
	public void init(Team t) {

		setTeam(t);
		points = 0;
		resources = 0;
		path = new Path();
		
		costVector = new int[Simulator.simParams.numOfAbilitiesPerAgent];
		for (int i=0;i<Simulator.simParams.numOfAbilitiesPerAgent;i++)
			costVector[i] = -1;
		
		// init the agents positions
		agPos = new RowCol[Simulator.simParams.numOfAgentsPerTeam];
		for (int i=0;i<Simulator.simParams.numOfAgentsPerTeam;i++)
			this.agPos[i] = new RowCol(-1,-1);
		
		// init the own positions
		pos = new RowCol(-1, -1);
		
		// init the agent's  goal
		myGoal = null;
		
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
	 * We can pass all the information to the agent, but it can filer them
	 * so that it can have partial observability
	 * 
     * @param board The current state of the board
     * @param costVectors The cost vectors of all the agents
     * @param goals The goals for all the agents
     * @param agentsPos the current position of all the agents within
     *        the team
     */
	public void perceive(Board board, int[][] costVectors, Goal[] goals, RowCol[] agentsPos) {
		// Keep the necessary information private
		
		// Update the cost vector		
		for (int i=0;i<costVectors[id].length;i++)
			this.costVector[i] = costVectors[id][i];
		
		// Update the agents positions
		for (int i=0;i<agentsPos.length;i++)
			this.agPos[i] = agentsPos[i];
		
		// Update the own positions
		pos = agPos[id];
		
		// Update the agent's  goal
		myGoal = goals[id];
		
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
	
	public RowCol pos() {
		return pos;
	}

	public Team team() {
		return team;
	}
}

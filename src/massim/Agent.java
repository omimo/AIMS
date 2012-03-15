package massim;

/**
 * Agent.java An abstract class for all the agents to be used in the simulator
 * 
 * @author Omid Alemi
 * @version 2.0 2011/10/31
 */
public abstract class Agent {

	public static int cellReward;
	public static int achievementReward;
	public static int helpOverhead;
	public static int calculationCost;
	
	protected static enum AgGameStatCode {
		READY, REACHED_GOAL, RESOURCE_BLOCKED, BLOCKED
	}
	
	protected static enum AgCommStatCode {
		DONE, NEEDING_TO_SEND, NEEDING_TO_REC 
	}
	
	protected static enum actionType {
		OWN, HELP_ANOTHER, HAS_HELP, SKIP, FORFEIT  
	}

	private int id;

	private int[] actionCosts;
	private Path path;

	private int resourcePoints = 0;

	private RowCol pos;
	private RowCol goalPos;
	private Board theBoard;
	
	private CommMedium communicationMedium; 

	private actionType thisRoundAction = actionType.SKIP;
	
	
	/* 
	 * Experience stuff
	 * */
	
	private int[] experience;
	private boolean useExperience;
	
	/**
	 * The constructor.
	 * 
	 * The team will pass the id to the agent.
	 * 
	 * @param id			The id of the agent being created.
	 */
	public Agent(int id, CommMedium comMed) {
		this.id = id;
		communicationMedium = comMed;
		goalPos = null;
		pos = null;
		theBoard = null;
		path = null;
		useExperience = false;
	}

	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param actionCosts				The agent's action costs vector
	 */
	public void initializeRun(int[] actionCosts) {
				
		theBoard = null;

		this.actionCosts = new int[actionCosts.length];
		System.arraycopy(actionCosts, 0, this.actionCosts, 0,
				actionCosts.length);			
	}
	
	/**
	 * Initializes the agent for a new match within the current run.
	 * 
	 * @param initialPosition			The initial position of this agent
	 * @param goalPosition				The goal position for this agent
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	public void initializeMatch(RowCol initialPosition, RowCol goalPosition,
			 int initResourcePoints) {
		
		path = null;
		this.pos = initialPosition;
		this.goalPos = goalPosition;
		resourcePoints = 0;
		incResourcePoints(initResourcePoints);
		
		experience = new int[SimulationEngine.numOfColors];
		for (int i=0; i<experience.length; i++){
			experience[i]=0;
		}
	}

	/** 
	 * Initializes the agent for a new round of the game.
	 * 
	 * 
	 * @param board						The game board
	 * @param actionCostsMatrix			The matrix containing the action costs
	 * 									for all the agents in the team (depends
	 * 									on the level of mutual awareness in the
	 * 									team)
	 */
	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		this.theBoard = board;

	}

	/**
	 * Enables the agent to send its outgoing messages (if any)
	 * 
	 * 
	 * @return							The current state of the agent 
	 */
	protected AgCommStatCode sendCycle() {
		
		return AgCommStatCode.DONE;
	}

	/**
	 * Enables the agent to receive its incoming messages (if any)
	 * 
	 * 
	 * @return							The current state of the agent 
	 */
	protected AgCommStatCode receiveCycle() {

		
		return AgCommStatCode.DONE;
	}

	/**
	 * Enables the agent to perform any actions for this round of
	 * the game.
	 * 
	 * @return							The status of the agent after 
	 * 									this round
	 */
	protected AgGameStatCode finalizeRound() {
		
		return AgGameStatCode.READY;
	}
	
	/**
	 * Enables the agent to get their id 
	 * 
	 * @return							The id of the agent
	 */
	protected int id() {
		return id;
	}

	/**
	 * Returns the amount reward points that the agent has earned.
	 *  
	 * This uses the Agent.calcRewardPoints() to get the reward 
	 * points.
	 *  
	 * @return							The reward points
	 */
	public int rewardPoints() {		
	
		return calcRewardPoints(resourcePoints,pos);
	}

	/**
	 * Calculates the agent's reward points based on the given 
	 * resources and position.
	 * 
	 * If the agent has reached the goal, then it will be rewarded
	 * the amount specified by 'achievementReward' plus the amount
	 * of resource points it has left.
	 * 
	 * If the agent has not reached the goal, it will be rewarded
	 * the amount specified by 'cellReward' for each cell it has
	 * passed.
	 * 
	 * @param resources					The resource points left
	 * @param position					The position on the path
	 * @return							The award points
	 */
	protected int calcRewardPoints(int resources, RowCol position) {
		if (position.equals(path.getEndPoint()))
			return Agent.achievementReward + resources;
		else
			return (path.getIndexOf(position)) * Agent.cellReward;
			/* uses the index of position, starting from 0;
		     * as if the agent has not moved at all, there should
		     * be no reward points 
		     */
	}
	
	/**
	 * Returns the amount of resource points that the agent has earned.
	 * 
	 * @return							The resource points
	 */
	protected int resourcePoints() {
		return resourcePoints;
	}

	/**
	 * Increases the resource points by the specified amount.
	 * 
	 * @param amount					The amounts to be added
	 */
	public void incResourcePoints(int amount) {
		resourcePoints += amount;
	}

	/**
	 * Decreases the resource points by the specified amount.
	 * 
	 * @param amount					The amounts to be subtracted
	 */
	protected void decResourcePoints(int amount) {
		if (resourcePoints - amount < 0)
			System.err.println("["+this.getClass().getSimpleName()+id+"]ERROR: decreasing too much resource points!");
		else
			resourcePoints -= amount;
	}

	/**
	 * Enables the agent to access its current position.
	 * 
	 * @return							The current position
	 */
	protected RowCol pos() {
		return pos;
	}

	/**
	 * Sets the position of the agent
	 * 
	 * @param newPos					The new position
	 */
	protected void setPos(RowCol newPos) {
		pos = newPos;
	}
	
	/**
	 * Enables the agent to access its goal position.
	 * 
	 * @return 							The position of the goal
	 */
	protected RowCol goalPos() {
		return goalPos;
	}

	/**
	 * Enables the agent to access its action costs vector.
	 * 
	 * @return 							The action costs vector 
	 * 									of the agent
	 */
	protected int[] actionCosts() {

		return actionCosts;
	}

	/**
	 * Returns the cost of a given cell for this agent
	 * 
	 * @param cell			            The position of the cell
	 * @return 							The cost associated with 
	 * 									the color of the given 
	 * 									cell
	 */
	protected int getCellCost(RowCol cell) {
		int color = theBoard.getBoard()[cell.row][cell.col];
		int originalCost = actionCosts()[color];
		if (useExperience && originalCost>40){
			int exp = experience[theBoard.getBoard()[cell.row][cell.col]];
			int discount = exp * 10;
			if ((originalCost-discount)>40) return originalCost-discount;
			else return 40;
		}
		else{
			return originalCost;
		}
		
	}

	/**
	 * Returns the cost of a given cell based on the given actions
	 * cost vector. 
	 * 
	 * @param cell			            The position of the cell
	 * @param actCost		            The action costs vector
	 * @return 							The cost associated with 
	 * 									the color of the given 
	 * 									cell
	 */
	protected int getCellCost(RowCol cell, int[] actCost) {

		int color = theBoard.getBoard()[cell.row][cell.col];
		return actCost[color];
	}

	/**
	 * Enables the agent to access to the game board 
	 * 
	 * @return 							The instance of the board
	 */
	protected Board theBoard() {
		return theBoard;
	}

	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm V2.
	 * 
	 * The method uses the agents position as the starting point and the goal
	 * position as the ending point of the path.
	 */
	protected void findPath() {
		PolajnarPath2 pp = new PolajnarPath2();
		Path shortestPath = new Path(pp.findShortestPath(
				boardToCosts(theBoard.getBoard(), actionCosts), pos, goalPos));
		path = new Path(shortestPath);
	}

	/** 
	 * Creates a two dimensional array representing the cell cost
	 * based on the given action costs vector.
	 * 
	 * This method is used by the path finding algorithm.
	 * 
	 * @param board						The game board setting
	 * @param actionCosts	            The action costs
	 * @return 							The 2dim array of costs
	 */
	private int[][] boardToCosts(int[][] board, int[] actionCosts) {
		int[][] costs = new int[board.length][board[0].length];

		for (int i = 0; i < costs.length; i++)
			for (int j = 0; j < costs[0].length; j++)
				costs[i][j] = actionCosts[board[i][j]];

		return costs;
	}

	/**
	 * Enables the agent to access its path
	 * 
	 * @return 							The instance of the path.
	 */
	protected Path path() {
		return path;
	}
	

	
	/**
	 * Sets the type of the action that is going to be performed
	 * in this round.
	 * 
	 * @param a						The action type
	 */
	protected void setRoundAction(actionType a) {
		thisRoundAction = a;
	}
	
	/**
	 * Enables the agent to get the action type for current round.
	 * 
	 * @return						The action type
	 */
	protected actionType getRoundAction() {
		return thisRoundAction;
	}
	
	/**
	 * Enables the agent to perform its own action. 
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true if successful/false o.w.
	 */
	protected boolean doOwnAction() {
		
		return true;
	}
	
	/**
	 * Enables the agent to perform an action on behalf of another 
	 * agent (Help). 
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true if successful/false o.w.
	 */
	protected boolean doHelpAnother() {
		
		return true;
	}
	
	/**
	 * Enables the agent do any bookkeeping while receiving help.
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true if successful/false o.w.
	 */
	protected boolean doGetHelpAction() {
		
		return true;
	}
	
	/**
	 * Checks whether the agent has reached the goal or not.
	 * 
	 * @return				true if has reached the goal /
	 * 						false o.w.
	 */
	protected boolean reachedGoal() {
		return path().getEndPoint().equals(pos());
	}
	
	protected CommMedium commMedium() {
		return communicationMedium;
	}
	
	protected boolean act() {
	
		boolean result = false;
		
		switch (thisRoundAction) {
		case OWN:
			result = doOwnAction();
			break;
		case HAS_HELP:
			result = doGetHelpAction();
			break;
		case HELP_ANOTHER:
			result = doHelpAnother();
			break;
		case SKIP:
			result = true;
			break;
		case FORFEIT:		
			result = false;
			break;
		}
		
		return result;
	}
	
	/**
	 * Switch on/off the use of experience when evaluating a cell cost
	 * 
	 * @param b
	 */
	public void useExperience(boolean b) {
		useExperience = b;
	}
	
	/**
	 * Increases the experience of the agent for the given color by one.
	 * 
	 * @param colorIndex
	 */
	protected void incExperience(int colorIndex) {
		experience[colorIndex]++;
	}
	
	/**
	 * Calculating the level of past experience with a specific action
	 * 
	 */
	protected int pastExperience(int colorIndex){
		return experience[colorIndex];
	}
}

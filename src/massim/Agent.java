package massim;

import massim.ExperimentLogger.LogType;

/**
 * Agent.java An abstract class for all the agents to be used in the simulator
 * 
 * @author Omid Alemi
 * @version 2.1 2012/01/19
 */
public abstract class Agent {


	public static int calculationCost;
	public static double planCostCoeff;
	
	protected static enum AgGameStatCode {
		READY, REACHED_GOAL, RESOURCE_BLOCKED, BLOCKED
	}
	
	protected static enum AgCommStatCode {
		DONE, NEEDING_TO_SEND, NEEDING_TO_REC 
	}
	
	//Mojtaba, 2014/06/27, added HELP_GET_HELP
	protected static enum actionType {
		OWN, HELP_ANOTHER, HAS_HELP, SKIP, FORFEIT, HELP_GET_HELP  
	}

	private int id;

	protected int[] actionCosts;
	protected Path path;

	protected int resourcePoints = 0;


	protected TeamTask tt;
	private int mySubtask;
	int[] subtaskAssignments;
	int[] actionCostsRange;
	
	//private RowCol pos;
	protected RowCol[] currentPositions;
	protected Board theBoard;
	
	private CommMedium communicationMedium; 

	private actionType thisRoundAction = actionType.SKIP;
	
	
	/* TODO: make these private! */
	public int numOfHelpReq = 0;
	public int numOfHelpOffer = 0;	//Mojtaba, 2014/07/07
	public int numOfBids = 0;
	public int numOfSucOffers = 0;
	public int numOfUnSucHelpReq = 0;
	public boolean remainingResInRewards = false;
	public int numOfSwapSuccess = 0;
	public int numOfSwapReq = 0;
	public int numOfSwapBid = 0;
	public int numOfSwapAbort = 0;
	public int numOfReplans = 0;
	public int replanCosts = 0;
	
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
		tt = null;
		theBoard = null;
		path = null;
		mySubtask = -1;
		//pos = null;
	}

	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param tt						The team task setting
	 * @param subtaskAssignments		The subtask assignments for the team.
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	public void initializeRun(TeamTask tt, int[] subtaskAssignments ,
			RowCol[] currentPos,
			int[] actionCosts, int initResourcePoints, int[] actionCostsRange) {
		
		this.tt = null;
		theBoard = null;
		path = null;
		mySubtask = -1;

		this.tt = tt;
		this.subtaskAssignments = new int[subtaskAssignments.length];
		System.arraycopy(subtaskAssignments, 0, this.subtaskAssignments, 
				0, subtaskAssignments.length);
		
		this.actionCostsRange = new int[actionCostsRange.length];
		System.arraycopy(actionCostsRange, 0, this.actionCostsRange, 
				0, actionCostsRange.length);
		
		//Denish, replaced logic for subtask.
		int i=0;
		/*while (subtaskAssignments[i]!=id && i < subtaskAssignments.length)
			i++;
		mySubtask = i;*/
		while(subtaskAssignments.length > i) {
			if(subtaskAssignments[i] == id) {
				mySubtask = i;
				break;
			}
			i++;
		}
		
		if (mySubtask != -1)
			currentPos[mySubtask] = tt.startPos[mySubtask];
		
		this.currentPositions = currentPos;
		
		this.actionCosts = new int[actionCosts.length];
		System.arraycopy(actionCosts, 0, this.actionCosts, 0,
				actionCosts.length);
		
		resourcePoints = 0;
		incResourcePoints(initResourcePoints);
		
		//Denish, 2014/03/26
		this.initResourcePoints = initResourcePoints;
		
		//Denish, 2014/04/13
		roundNumber = 0;
		/*
		numOfHelpReq = 0;
		numOfBids = 0;
		numOfSucOffers = 0;
		numOfUnSucHelpReq = 0;
		*/
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
		
		//logInf("Total number of help requests = " + numOfHelpReq);
		//logInf("Total number of bids = " + numOfBids);
		//logInf("Total successful offers = " + numOfSucOffers);
		//logInf("Total unsuccessful help requests = " + numOfUnSucHelpReq);
		//logInf("Total number of swap requests = " + numOfSwapReq);
		//logInf("Total number of swap bids = " + numOfSwapBid);
		//logInf("Total number of unsuccessful swaps = " + numOfSwapAbort);
		//logInf("Total number of swaps = " + numOfSwapSuccess);
		//logInf("Total number of replans = " + numOfReplans);
		//logInf("Total replan costs = " + replanCosts);
		logInf("Resource points = " + resourcePoints());
		//Denish, 2014/04/13
		roundNumber++;
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

//	/**
//	 * Returns the amount reward points that the agent has earned.
//	 *  
//	 * This uses the Agent.calcRewardPoints() to get the reward 
//	 * points.
//	 *  
//	 * @return							The reward points
//	 */
//	public int rewardPoints() {		
//	
//		return calcRewardPoints(resourcePoints,
//				currentPos[mySubtask]);
//	}

	/**
	 * Calculates the agent's reward points based on the given 
	 * resource points and position.
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
			if(remainingResInRewards)
				return TeamTask.achievementReward + resources;
			else
				return TeamTask.achievementReward;
		else
			return (path.getIndexOf(position)) * TeamTask.cellReward;
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
		//return pos;
		return currentPositions[mySubtask];
	}

	/**
	 * Sets the position of the agent
	 * 
	 * @param newPos					The new position
	 */
	protected void setPos(RowCol newPos) {
		//pos = newPos;
		currentPositions[mySubtask] = newPos;
	}
	
	/**
	 * Enables the agent to access its goal position.
	 * 
	 * @return 							The position of the goal
	 */
	protected RowCol goalPos() {
		return tt.goalPos[mySubtask];
	}
	
	//Denish, added due to mysubtask change
	/**
	 * Enables the agent to access its goal position.
	 * 
	 * @return 							The position of the goal
	 */
	protected RowCol startPos() {
		return tt.startPos[mySubtask];
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
	 * Enables the agent to access full action costs vector.
	 * 
	 * @return 							The action costs vector 
	 * 									of the agent
	 */
	protected int[] actionCostsRange() {

		return actionCostsRange;
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
		return actionCosts()[color];
	}
	
	/**
	 * Returns the color index of a given cell for this agent
	 * 
	 * @param cell			            The position of the cell
	 * @return 							The color index related to the color of the given 
	 * 									cell
	 */
	protected int getCellColor(RowCol cell) {
		return theBoard.getBoard()[cell.row][cell.col];
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
		if (mySubtask != -1)
		{
			PolajnarPath2 pp = new PolajnarPath2();
			Path shortestPath = new Path(pp.findShortestPath(
					boardToCosts(theBoard.getBoard(), actionCosts), 
					currentPositions[mySubtask], goalPos()));
			path = new Path(shortestPath);
			if(path.getNumPoints() == 0) {
				System.err.println("Empty path" + currentPositions[mySubtask()] + " " + goalPos());
			}
			//Mojtaba
			decResourcePoints(planCost());
		}
		else 
			path = null;
	}

	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm V2.
	 * 
	 * The method uses s as the starting point and the r as the ending point 
	 * of the path.
	 */
	protected Path findPath(RowCol s, RowCol r) {
		PolajnarPath2 pp = new PolajnarPath2();
		Path shortestPath = new Path(pp.findShortestPath(
				boardToCosts(theBoard.getBoard(), actionCosts), 
				s, r));
		return shortestPath;
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
	 * Enables the agent to perform an action on behalf of another 
	 * agent (Help) and to do any bookkeeping while receiving help (GetHelp).
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true
	 */
	//Mojtaba, 2014/06/27
	protected boolean doHelpGetHelp() {
		
		return true;
	}
	
	/**
	 * Checks whether the agent has reached the goal or not.
	 * 
	 * @return				true if has reached the goal /
	 * 						false o.w.
	 */
	protected boolean reachedGoal() {
		if (mySubtask == -1)
			return false;
		else
			return path().getEndPoint().equals(pos());
	}
	
	protected CommMedium commMedium() {
		return communicationMedium;
	}
	
	protected boolean act() {
	
		boolean result = false;
		//Denish, 2014/03/30, added strLastAction
		switch (thisRoundAction) {
		case OWN:
			setLastAction("Self");
			result = doOwnAction();
			break;
		case HAS_HELP:
			result = doGetHelpAction();
			break;
		case HELP_ANOTHER:
			result = doHelpAnother();
			break;
		//Mojtaba, 2014/06/27
		case HELP_GET_HELP:
			result = doHelpGetHelp();
			break;			
		case SKIP:
			setLastAction("Skipped");
			result = true;
			break;
		case FORFEIT:		
			setLastAction("Forfeit");
			result = false;
			break;
		}
		
		return result;
	}
	
	protected void mySubtask(int s) {
		if (s>=0 && s< Team.teamSize)
			mySubtask = s;
		else
			mySubtask = -1;
	}
	
	protected int mySubtask() {
		return mySubtask;
	}

	protected void doPlan() {
		if (mySubtask() != -1)
			findPath();
	}
	
	//Denish, 2014/03/26
	protected int initResourcePoints;
	private String strLastAction;
	private int roundNumber = 0;
	protected String getLastAction() {
		return strLastAction;
	}
	public void setLastAction(String strLastAction) {
		this.strLastAction = strLastAction;
	}
	ExperimentLogger logger; String agentIndex;
	public void setLogger(ExperimentLogger logger, int teamindex, int index) {
		this.logger = logger;
		this.agentIndex = teamindex + "-" + index;
	}
	protected void logInf(String msg) {
		if(logger != null)
			logger.logEvent(LogType.Agent, agentIndex, "[Ag# " + id() + ", Round# " + roundNumber + "]: " + msg);
	}
	
	/**
	 * Calculates the cost of replanning (finding new path) based on the complexity of the PolajnarPath algorithm.
	 * n,m are length and width of the remaining board.
	 * 
	 * @author Mojtaba
	 * 
	 */
	protected int planCost() {
	int n = Math.abs(goalPos().row - currentPositions[mySubtask].row);
	int m = Math.abs(goalPos().col - currentPositions[mySubtask].col);
	int cost = (int)Math.round(planCostCoeff * (n+m) * (n+m));
	//Mojtaba, 2014/04/20
	if (cost < 1)
		return 1;
	if (cost > 10)
		return 10;
	return cost;	
	}
	
	/**
	 * Swaps subtask assignment of two agents.
	 * 
	 * @param agentId		Id of an agent to swap subtask with.
	 */
	protected void swapSubTaskAssignment(int agent1Id, int agent2Id)
	{
		for(int index = 0; index < subtaskAssignments.length; index++) {
			if(subtaskAssignments[index] == agent2Id) {
				subtaskAssignments[index] = agent1Id;
			} else if(subtaskAssignments[index] == agent1Id) {
				subtaskAssignments[index] = agent2Id;
			}
		}
	}
	
	/**
	 * Swaps subtask assignment with particular agent.
	 * 
	 * @param agentId		Id of an agent to swap subtask with.
	 */
	protected void swapSubTaskAssignment(int agentId)
	{
		for(int index = 0; index < subtaskAssignments.length; index++) {
			if(subtaskAssignments[index] == agentId) {
				subtaskAssignments[index] = id();
				mySubtask(index);
			} else if(subtaskAssignments[index] == id()) {
				subtaskAssignments[index] = agentId;
			}
		}
	}
	
	protected boolean canSwap() {
		return (resourcePoints() >= (Team.broadcastCost + TeamTask.swapOverhead + planCost()));	
	}
}

package massim.agents.nohelp;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Path;
import massim.PolajnarPath2;
import massim.RowCol;
import massim.SimulationEngine;
import massim.TeamTask;

/**
 * The NO-HELP Agent Implementation
 * 
 * @author Omid Alemi
 * @version 2.0  2011/11/11
 */
public class NoHelpAgent extends Agent {
	
	private boolean dbgInf = false;
	private boolean dbgErr = true;

	enum NoHelpAgentStates {S_INIT, R_MOVE, R_BLOCKED, R_SKIP,
		
		//Denish, 2014/04/26, swap
		R_INIT, S_INIT_SW,
		SW_R_GET_REQ, SW_S_AWAIT_RESPONSE,
		SW_R_GET_BIDS, SW_S_RESPOND_TO_REQ,
		SW_S_ANNOUNCE, SW_R_AWAIT_OUTCOME, SW_S_AWAIT_OUTCOME,
		SW_R_COMPLETE_SWAP	
	};
	
	NoHelpAgentStates state;
	
	private int[][] oldBoard;
	protected double disturbanceLevel;
	
	/**
	 * The constructor
	 * 
	 * @param id			The given id of the agent
	 */
	public NoHelpAgent(int id, CommMedium comMed) {
		
		super(id, comMed);
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
			int[] actionCosts,int initResourcePoints, int[] actionCostsRange) {
		
		super.initializeRun(tt,subtaskAssignments,
				currentPos,actionCosts,initResourcePoints, actionCostsRange);		
		
		logInf("Initialized for a new run.");
		logInf("My initial resource points = "+resourcePoints());		
		logInf("My goal position: " + goalPos().toString());
		
		oldBoard = null;
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
	@Override
	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		
		super.initializeRound(board, actionCostsMatrix);				
		logInf("Starting a new round ...");
		
		if (path() == null)
		{		
			findPath();			
			logInf("Initial Planning: Chose this path: "+ path().toString());
		}
		
		logInf("My current position: " + pos().toString());
		
		state = NoHelpAgentStates.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
		
		disturbanceLevel = calcDistrubanceLevel();
		logInf("The estimated disturbance level on the board is " + disturbanceLevel);
	}
	
	/**
	 * The send cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("Send Cycle");		
		
		switch (state) {
		case S_INIT:
			if (!reachedGoal())
			{
				RowCol nextCell = path().getNextPoint(pos());
				int cost = getCellCost(nextCell);
				if (cost  <= resourcePoints() && !pos().equals(nextCell))
					setState(NoHelpAgentStates.R_MOVE);
				else
					setState(NoHelpAgentStates.R_BLOCKED);				
			}
			else
				setState(NoHelpAgentStates.R_SKIP);
			
			returnCode = AgCommStatCode.NEEDING_TO_REC;
			break;		
		default:
			logErr("Undefined state: " + state.toString());
		}
		
		return returnCode;
	}
	
	/**
	 * The receive cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode receiveCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		
		logInf("Receive Cycle");		
		
		switch (state) {		
		case R_MOVE:	
			logInf("Setting current action to do my own move");
			setRoundAction(actionType.OWN);			
			returnCode = AgCommStatCode.DONE;
			break;	
		case R_BLOCKED:
			setRoundAction(actionType.FORFEIT);
			returnCode = AgCommStatCode.DONE;
			break;
		case R_SKIP:
			setRoundAction(actionType.SKIP);
			returnCode = AgCommStatCode.DONE;
			break;
		default:	
			logErr("Undefined state: " + state.toString());
		}
		
		return returnCode;
	}

	/**
	 * Finalizes the round by moving the agent.
	 * 
	 * Also determines the current state of the agent which can be
	 * reached the goal, blocked, or ready for next round.  
	 * 
	 * @return 						Returns the current state 
	 */
	@Override
	protected AgGameStatCode finalizeRound() {
		
		logInf("Finalizing the round ...");
		
		keepBoard();
		
		boolean succeed = act();
		
		if (reachedGoal())
		{
			logInf("Reached the goal");
			return AgGameStatCode.REACHED_GOAL;
		}
		else
		{
			if (succeed) 
				return AgGameStatCode.READY;
			else  /*TODO: The logic here should be changed!*/
			{
				logInf("Blocked!");
				return AgGameStatCode.BLOCKED;			
			}
		}					
	}
		
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	protected void logInf(String msg) {
		if (dbgInf)
			System.out.println("[NoHelpAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.err.println("[xx][NoHelpAgent " + id() + 
							   "]: " + msg);
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	protected void setState(NoHelpAgentStates newState) {
		logInf("In "+ state.toString() +" state");
		state = newState;
		logInf("Set the state to +"+state.toString());
	}

	/**
	 * Agent's move action.
	 * 
	 * Moves the agent to the next position if possible
	 * 
	 * TODO: Needs to be extended to perform help.
	 * 
	 * @return
	 */
	private boolean move() {
		
		RowCol nextCell = path().getNextPoint(pos());
		if (pos().equals(nextCell))
		{
			logErr("Can not move from "+pos() +" to itself!"); 			
			return false;
		}
		else
		{
			logInf("Moved from "+pos() +" to "+ nextCell);
			
			int cost = getCellCost(nextCell);
			decResourcePoints(cost);
			setPos(nextCell);				
			return true;
		}
	}
	
	/**
	 * The agent performs its own action (move) here.
	 * 
	 * @return					The same as what move() returns.
	 */
	@Override
	protected boolean doOwnAction() {
		
		return move();		
	}
	
	/**
	 * Calculates the average of the given integer array.
	 * 
	 * @return						The average.
	 */
	protected double getAverage(int[] array) {
		int sum = 0;
		for (int i=0;i<array.length;i++)
			sum+=array[i];
		return (double)sum/array.length;
	}
	
	/**
	 * Calculates the disturbance level of the board.
	 * 
	 * This compares the current state of the board with the stored state
	 * from the previous round.
	 * 
	 * @return				The level of disturbance.
	 */
	private double calcDistrubanceLevel() {
		
		if (oldBoard == null)
			return 0.0;
		
		int changeCount = 0;		
		for (int i=0;i<theBoard().rows();i++)
			for (int j=0;j<theBoard().cols();j++)
				if (theBoard().getBoard()[i][j] != oldBoard[i][j])
					changeCount++;	
		double change = (double)changeCount / (theBoard().rows() * theBoard().cols());
		//Mojtaba, 2014/04/20
		return change * (double)SimulationEngine.numOfColors/(SimulationEngine.numOfColors - 1);
	}
	
	/**
	 * Keeps the current state of the board for calculating the disturbance
	 * in the next round of the game.
	 * 
	 * This copied theBoard into oldBoard. 
	 */
	private void keepBoard() {
		
		int rows = theBoard().rows();
		int cols = theBoard().cols();
		
		if (oldBoard == null) /* first round */
			oldBoard = new int[rows][cols];
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				oldBoard[i][j] = theBoard().getBoard()[i][j];	
	}

	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm V2.
	 * 
	 * The method uses the agents position as the starting point and the goal
	 * position as the ending point of the path.
	 * 
	 * @author Mojtaba
	 */
	
	@Override
	protected void findPath() {
		if (mySubtask() != -1)
		{
			PolajnarPath2 pp = new PolajnarPath2();
			Path shortestPath = new Path(pp.findShortestPath(
					estimBoardCosts(theBoard.getBoard()), 
					currentPositions[mySubtask()], goalPos()));
			path = new Path(shortestPath);

			int pCost = planCost();
			replanCosts += pCost;
			decResourcePoints(pCost);
		}
		else 
			path = null;
	}

	/**
	 * Returns a two dimensional array representing the estimated cost
	 * of cells with i, j coordinates
	 * 
	 * @author Mojtaba
	 */
	private int[][] estimBoardCosts(int[][] board) {
		
		int[][] eCosts = new int[board.length][board[0].length];
		
		for (int i = 0; i < eCosts.length; i++)
			for (int j = 0; j < eCosts[0].length; j++) {
				
				eCosts[i][j] = estimCellCost(i ,j);
			}
						
		return eCosts;		
	}

	/**
	 * Returns estimated cost of a cell with k steps from current position
	 * 
	 * @param i				cell coordinate
	 * @param j				cell coordinate
	 * @author Mojtaba
	 */	
	private int estimCellCost(int i, int j) {
		double sigma = 1 - disturbanceLevel;
		double m = getAverage(actionCosts());
		int k = Math.abs((currentPositions[mySubtask()].row - i)) + Math.abs((currentPositions[mySubtask()].col - j));
		
		int eCost = (int) (Math.pow(sigma, k) * actionCosts[theBoard.getBoard()[i][j]]  + (1 - Math.pow(sigma, k)) * m);
		return eCost;		
	}
	
}

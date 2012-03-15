package massim.agents.nohelp;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.RowCol;

/**
 * The NO-HELP Agent Implementation
 * 
 * @author Omid Alemi
 * @version 2.0  2011/11/11
 */
public class NoHelpAgent extends Agent {
	
	private boolean dbgInf = false;
	private boolean dbgErr = true;

	enum NoHelpAgentStates {S_INIT, R_MOVE, R_BLOCKED, R_SKIP};
	
	NoHelpAgentStates state;
	
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
	 * @param actionCosts				The agent's action costs vector	 
	 */
	@Override
	public void initializeRun(int[] actionCosts) {		
		super.initializeRun(actionCosts);		
		
		logInf("Initialized for a new run.");
	}
		
	/**
	 * Initializes the agent for a new match within current run
	 * 
	 * @param initialPosition			The initial position of this agent
	 * @param goalPosition				The goal position for this agent
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	@Override
	public void initializeMatch(RowCol initialPosition, RowCol goalPosition,
			 int initResourcePoints) {
		super.initializeMatch(initialPosition, goalPosition, initResourcePoints);
		
		logInf("Initializing for a new match");
		logInf("My initial resource points = "+resourcePoints());		
		logInf("My goal position: " + goalPos().toString());
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
		
		logInf("My current position: " + pos().toString());
		if (path() == null)
		{		
			findPath();			
			logInf("Chose this path: "+ path().toString());
		}
		
		state = NoHelpAgentStates.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
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
				if (cost  <= resourcePoints())
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
	private void logInf(String msg) {
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
	private void setState(NoHelpAgentStates newState) {
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
			incExperience(theBoard().getBoard()[nextCell.row][nextCell.col]);
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
	
}

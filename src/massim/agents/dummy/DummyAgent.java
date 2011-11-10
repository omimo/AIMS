package massim.agents.dummy;

import java.util.Random;

import massim.Agent;
import massim.Board;
import massim.RowCol;

public class DummyAgent extends Agent {
	
	private boolean debuggingInf = true;
	
	private int procrastinateCount;
	private int procrastinateLevel;
	

	enum DummyStates {S_INIT, S_PROC, R_PROC, R_MOVE, R_BLOCKED};
	
	DummyStates state;
	
	/**
	 * The constructor
	 * 
	 * @param id			The given id of the agent
	 */
	public DummyAgent(int id) {
		super(id);
	}
	
	
	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param initialPosition			The initial position of this agent
	 * @param goalPosition				The goal position for this agent
	 * @param actionCosts				The agent's action costs vector
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	@Override
	public void initializeRun(RowCol initialPosition, RowCol goalPosition,
			int[] actionCosts, int initResourcePoints) {
		
		super.initializeRun(initialPosition, goalPosition, 
				actionCosts,initResourcePoints);		
		
		logInf("Initialized for a new run.");
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
		
		state = DummyStates.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
		
		procrastinateLevel = (new Random()).nextInt(4);
		procrastinateCount = 0;
	}
	
	/**
	 * A dummy send cycle method.
	 * 
	 * Will alternate between send and receive states.
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("Send Cycle");		
		
		switch (state) {
		case S_INIT:			
			RowCol nextCell = path().getNextPoint(pos());
			int cost = getCellCost(nextCell);
			if (cost  <= resourcePoints())
				setState(DummyStates.R_PROC);
			else
				setState(DummyStates.R_BLOCKED);			
			returnCode = AgCommStatCode.NEEDING_TO_REC;
			break;
		case S_PROC:
			procrastinateCount++;
			if (procrastinateCount > procrastinateLevel)
				setState(DummyStates.R_MOVE);
			else
				setState(DummyStates.R_PROC);
			returnCode = AgCommStatCode.NEEDING_TO_REC;
			break;			
		default:
			logErr("Undefined state: " + state.toString());
		}
		
		return returnCode;
	}

	/**
	 * A dummy receive cycle method.
	 * 
	 * Will alternate between send and receive cycles for 3 times.
	 * Then will transit to a final state.
	 * 
	 */
	@Override
	protected AgCommStatCode receiveCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		
		logInf("Receive Cycle");		
		
		switch (state) {
		case R_PROC:
			setState(DummyStates.S_PROC);
			returnCode = AgCommStatCode.NEEDING_TO_SEND;
			break;			
		case R_MOVE:	
			logInf("Setting current action to do my own move");
			setRoundAction(actionType.OWN);			
			returnCode = AgCommStatCode.DONE;
			break;	
		case R_BLOCKED:
			setRoundAction(actionType.FORFEIT);
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
		
		if (pos().equals(goalPos()))
		{
			logInf("Reached the goal");
			return AgGameStatCode.REACHED_GOAL;
		}
		else
		{
			if (act())
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
		if (debuggingInf)
			System.out.println("[DummyAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (debuggingInf)
			System.out.println("[xxxxxxxxxxx][DummyAgent " + id() + 
							   "]: " + msg);
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(DummyStates newState) {
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
			logInf("Can not move from "+pos() +" to itself!"); 			
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
	 * The DummyAgent performs its own action (move) here.
	 * 
	 * @return					The same as what move() returns.
	 */
	@Override
	protected boolean doOwnAction() {
		return move();		
	}
	
}

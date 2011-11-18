package massim.agents.empathic;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.RowCol;


public class EmpathicAgent extends Agent {

	private boolean dbgInf = true;
	private boolean dbgErr = true;
	
	private enum EmpaticAgentState {S_INIT}
	private EmpaticAgentState state;
	
	public EmpathicAgent(int id, CommMedium comMed) {
		super(id, comMed); 
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
		
		state =EmpaticAgentState.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
		
	}
	
	/**
	 * The send cycle
	 * 
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("Send Cycle");		
		
		switch (state) {
		case S_INIT:			
			
			break;		
		default:
			logErr("Undefined state: " + state.toString());
		}
		
		return returnCode;
	}

	/**
	 * The receive cycle
	 * 
	 */
	@Override
	protected AgCommStatCode receiveCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		
		logInf("Receive Cycle");		
		
		switch (state) {
		
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
	 * level is turned on (dbgInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (dbgInf)
			System.out.println("[EmpathicAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (dbgErr).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.err.println("[xx][EmpathicAgent " + id() + 
							   "]: " + msg);
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(EmpaticAgentState newState) {
		logInf("In "+ state.toString() +" state");
		state = newState;
		logInf("Set the state to +"+state.toString());
	}

	/**
	 * The agent performs its own action (move) here.
	 * 
	 * @return					True if succeeded
	 */
	@Override
	protected boolean doOwnAction() {
		return true;		
	}
}

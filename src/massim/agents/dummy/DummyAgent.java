package massim.agents.dummy;

import massim.Agent;
import massim.Board;
import massim.RowCol;

public class DummyAgent extends Agent {
	
	private boolean debuggingInf = true;
	private int dbgSendCount = 1;
	private int dbgRecCount = 1;
	
	private boolean doMove=false;

	enum DummyStates {S_INIT, S_SEND, R_REC, R_MOVE, S_MOVE};
	
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
		logInf("Set the current state to +"+state.toString());
	}
	
	/**
	 * A dummy send cycle method.
	 * 
	 * Will alternate between send and receive states.
	 * 
	 */
	@Override
	protected agStateCode sendCycle() {
		
		logInf("Send Cycle #"+dbgSendCount);		
		dbgSendCount++;
		
		switch (state) {
		case S_INIT:
		case S_SEND:
			state = DummyStates.R_REC;
			logInf("In S_INIT/S_SEND state");
			logInf("Set the current state to +"+state.toString());
			break;					
		case S_MOVE:
			state = DummyStates.R_MOVE;
			logInf("In S_MOVE state");
			logInf("Set the current state to +"+state.toString());
			break;					
		}
		
		return agStateCode.DONE;
	}

	/**
	 * A dummy receive cycle method.
	 * 
	 * Will alternate between send and receive cycles for 3 times.
	 * Then will transit to a final state.
	 * 
	 */
	@Override
	protected agStateCode receiveCycle() {
		agStateCode returnCode = agStateCode.DONE;
		
		logInf("Receive Cycle #"+dbgRecCount);		
		dbgRecCount++;
		
		switch (state) {
		case R_REC:
			logInf("In R_REC state");
			if (dbgRecCount>3)
				{
					state = DummyStates.S_MOVE;
					logInf("Set the current state to +"+state.toString());
					returnCode = agStateCode.NEEDING_TO_SEND;
				}
			else
				{
					state = DummyStates.S_SEND;
					logInf("Set the current state to +"+state.toString());
					returnCode = agStateCode.NEEDING_TO_SEND;
				}
			
			break;
			
		case R_MOVE:
			logInf("In R_MOVE state (final)");
			doMove = true;
			dbgRecCount = 1;
			dbgSendCount = 1;
			returnCode = agStateCode.DONE;
			break;			
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
	protected agRoundCode finalizeRound() {
		
		logInf("Finalizing the round ...");
		RowCol oldPos = new RowCol(pos());
		
		if (pos().equals(goalPos()))
		{
			logInf("Reached the goal");
			return agRoundCode.REACHED_GOAL;
		}
		else
		{
			if (doMove)
			{				
				if(move())
					logInf("Moved from "+oldPos +" to "+ pos());
				else
				{
					logInf("Falied to Moved from "+oldPos +" to "+ 
							path().getNextPoint(oldPos));
					return agRoundCode.BLOCKED;
				}
			}
			else
				logInf("Staying at "+ pos());
			
			return agRoundCode.READY;
		}
		
		
	}
	
	
	/**
	 * Prints the log message into the output if the information debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (debuggingInf)
			System.out.println("[DummyAgent " + id() + "]: " + msg);
	}

}

package massim.agents.dummy;

import massim.Agent;
import massim.Board;
import massim.RowCol;

public class DummyAgent extends Agent {
	
	private boolean debuggingInf = true;
	private int dbgSendCount = 0;
	private int dbgRecCount = 0;
	
	private boolean doMove=false;

	enum DummyStates {S_INIT, S_SEND, R_REC, R_MOVE, S_MOVE};
	
	DummyStates state;
	
	public DummyAgent(int id) {
		super(id);

	}
	
	@Override
	protected void initializeRun(RowCol initialPosition, RowCol goalPosition,
			int[] actionCosts) {
		super.initializeRun(initialPosition, goalPosition, actionCosts);
		
		logInf("Initialized for a new run.");
		logInf("My initial position: " + initialPosition.toString());
		logInf("My goal position: " + goalPosition.toString());
		
		findPath();
		
		logInf("Chose this path: "+ path().toString());
	}
	
	@Override
	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		super.initializeRound(board, actionCostsMatrix);
		
		logInf("Starting a new round ...");
		
		state = DummyStates.S_INIT;
		logInf("Set the current state to +"+state.toString());
	}
	
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

	@Override
	protected agStateCode receiveCycle() {
		agStateCode returnCode = agStateCode.DONE;
		
		logInf("Receive Cycle #"+dbgRecCount);		
		dbgRecCount++;
		
		switch (state) {
		case R_REC:
			logInf("In R_REC state");
			if (dbgRecCount>4)
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
			returnCode = agStateCode.DONE;
			break;			
		}
		
		return returnCode;
	}

	@Override
	protected agRoundCode finalizeRound() {
		
		logInf("Finalizing the round ...");
		RowCol oldPos = new RowCol(pos());
		if (doMove)
		{				
			if(move())
				logInf("Moved from "+oldPos +" to "+ pos());
			else
				logInf("Falied to Moved from "+oldPos +" to "+ pos());
		}
		else
			logInf("Staying at "+ pos());
		
		if (pos().equals(goalPos()))
		{
			logInf("Reached the goal");
			return agRoundCode.REACHED_GOAL;
		}
		else
			return agRoundCode.READY;
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

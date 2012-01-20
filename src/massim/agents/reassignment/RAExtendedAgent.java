package massim.agents.reassignment;

import massim.CommMedium;
import massim.Message;
import massim.agents.nohelp.NoHelpAgent;


public class RAExtendedAgent extends NoHelpAgent {

	private boolean dbgInf = false;
	private boolean dbgErr = true;
	
	private static int leaderAgent = 0;
	
	private enum RAAgentStates {
		S_RACMD, R_RACMD, S_ESTIMATE, R_GATHER, S_ASSIGN, R_ASSIGN,R_CONT
	}
	
	private RAAgentStates state;
	
	private int RE_REASSIGN_CMD_MSG = 1;
	
	public RAExtendedAgent(int id, CommMedium comMed) {
		super(id, comMed);
		// TODO Auto-generated constructor stub
	}

	 /**
     * Handles the reassignment related states
     * 
     */
	private void reSendCycle() {
    	
		logInf("Reassignment Send Cycle");		
		
		switch (state) {
		case S_RACMD:
			boolean needReassign = false; //TODO: use the real condition
			
			if (needReassign)
				logInf("Need to reassign.");
			
			if (needReassign && 
				leaderAgent == id() &&
				canBCast() &&
				canAssign())				
			{
				logInf("Broadcasting reassignment command.");
				broadcastMsg(prepareREASSIGNMsg());
				setState(RAAgentStates.R_RACMD);
			}
			else
				setState(RAAgentStates.R_CONT);
			break;		
		default:
			logErr("Undefined state: " + state.toString());
		}
    }
		
	/**
	 * Handles the reassignment related receive states
	 */
	private void reReceiveCycle() {
	
		logInf("Receive Cycle");		
		
		switch (state) {		
		case R_CONT:
			setState(RAAgentStates.S_INIT);
			break;
		default:	
			logErr("Undefined state: " + state.toString());
		}

	}

	
	/**
	 * Prepares a reassignment command message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String prepareREASSIGNMsg() {
		
		Message helpReq = new Message(id(),-1,RE_REASSIGN_CMD_MSG);
		return helpReq.toString();
	}
	

	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (dbgInf)
			System.out.println("[RAAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.err.println("[xx][RAAgent " + id() + 
							   "]: " + msg);
	}
}

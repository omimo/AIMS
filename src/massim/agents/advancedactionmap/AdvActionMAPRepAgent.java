package massim.agents.advancedactionmap;

import massim.CommMedium;


public class AdvActionMAPRepAgent extends AdvActionMAPAgent {

	boolean dbgInf = false;
	boolean dbgErr = true;
	
	public static double WREP;
	
	public AdvActionMAPRepAgent(int id, CommMedium comMed) {
		super(id, comMed);
		// TODO Auto-generated constructor stub
	}

	/**
	 * The send cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		logInf("REP Send Cycle");	
		
		if (wellbeing() < AdvActionMAPRepAgent.WREP)
		{
			findPath();			
			logInf("Replanning: Chose this path: "+ path().toString());
		}
		
		return super.sendCycle();
	}
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (dbgInf)
			System.out.println("[AdvActionMAPRep " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.err.println("[xx][AdvActionMAPRep " + id() + 
							   "]: " + msg);
	}
}
